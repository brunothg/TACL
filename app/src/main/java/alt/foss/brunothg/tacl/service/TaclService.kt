/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.service

import alt.foss.brunothg.tacl.TaclApplication
import alt.foss.brunothg.tacl.bl.filter.BlockType
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.filter.CallFilter.Companion.filterCall
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.bl.log.FilterLogEntry
import alt.foss.brunothg.tacl.di.TaclServiceEntryPoint
import alt.foss.brunothg.tacl.misc.measureFirstEmissionTime
import alt.foss.brunothg.tacl.service.CallUtils.toTaclCall
import android.Manifest
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.concurrent.Volatile
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

/**
 * State of the call screening service.
 *
 * @property filters The list of call filters.
 * @property region The current region.
 */
private data class TaclServiceState(
    val filters: List<CallFilter>,
    val region: String?,
)

/**
 * State cache of the call screening service.
 * Automatically fetches the [TaclServiceState] and caches them.
 *
 * @param filters The list of call filters.
 * @param region The current region.
 * @property scope The coroutine scope.
 */
private class TaclServiceStateCache(
    filters: Flow<List<CallFilter>>,
    region: Flow<String?>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {

    private val cache: SharedFlow<TaclServiceState> = combine(
        filters,
        region,
    ) { callFilters, callRegion ->
        TaclServiceState(
            filters = callFilters,
            region = callRegion,
        )
    }.measureFirstEmissionTime { loadingTime ->
        Log.d("ServiceStateCache", "Service state loaded in $loadingTime")
    }.shareIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    fun getOrWait(): TaclServiceState {
        return cache.replayCache.firstOrNull()
            ?: runBlocking {
                cache.first()
            }
    }

    operator fun getValue(
        thisRef: Any?,
        property: kotlin.reflect.KProperty<*>
    ): TaclServiceState {
        return getOrWait()
    }

}

/**
 * Call screening service for TACL.
 */
@AndroidEntryPoint
class TaclService : CallScreeningService() {

    companion object {
        @Volatile
        private var stateCache: TaclServiceStateCache? = null
    }

    /**
     * Entry point for the service.
     * Hilt dependency injection.
     */
    private val entryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            TaclServiceEntryPoint::class.java
        )
    }

    /**
     * Repository for call filters.
     * Shortcut for [TaclServiceEntryPoint.callFilterRepository].
     */
    private val callFilterRepository
        get() = entryPoint.callFilterRepository()

    /**
     * Repository for the call filter log.
     * Shortcut for [TaclServiceEntryPoint.filterLogRepository].
     */
    private val filterLogRepository
        get() = entryPoint.filterLogRepository()

    /**
     * Repository for app settings.
     * Shortcut for [TaclServiceEntryPoint.appSettingsRepository].
     */
    private val appSettingsRepository
        get() = entryPoint.appSettingsRepository()

    private val stateCache: TaclServiceStateCache
        get() = synchronized(Companion) {
            TaclService.stateCache ?: run {
                Log.d("TaclService", "Initializing state cache")
                TaclServiceStateCache(
                    filters = callFilterRepository.callFilters,
                    region = appSettingsRepository.callRegion,
                ).also { TaclService.stateCache = it }
            }
        }

    override fun onCreate() {
        super.onCreate()
        requireNotNull(entryPoint as Any?)
        requireNotNull(stateCache as Any?)

        CoroutineScope(Dispatchers.Default).launch {
            stateCache.getOrWait()
        }
        Log.d("TaclService", "onCreate")
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    override fun onScreenCall(callDetails: Call.Details) {
        if (callDetails.callDirection != Call.Details.DIRECTION_INCOMING) {
            return
        }

        measureTime {
            val serviceState by stateCache

            val call = callDetails.toTaclCall(
                context = applicationContext,
                region = serviceState.region
            )
            Log.d("TaclService", "onScreenCall: $call")

            val aclResult = if (!call.isEmergencyCall)
                serviceState.filters.filterCall(call)
            else
                FilterAction.NOTHING
            Log.i("TaclService", "onScreenCall: $call -> $aclResult")


            saveFilterResultToLog(call, aclResult)
            sendSystemResponse(callDetails, aclResult)
        }.also { runTime ->
            Log.d("TaclService", "onScreenCall: finished in -> $runTime")
        }
    }

    /**
     * Send a [CallResponse] to the system based on the [FilterAction].
     * Has to be last step in the call screening process.
     * System may immediately stop the service after this function call.
     *
     * @param callDetails The call details.
     * @param action The action to take.
     */
    private fun sendSystemResponse(
        callDetails: Call.Details,
        action: FilterAction,
    ) {
        val callResponse = CallResponse.Builder().apply {

            action.block?.also {
                when (it) {
                    BlockType.IGNORE -> {
                        setDisallowCall(true)
                        setRejectCall(false)
                    }

                    BlockType.HANGUP -> {
                        setDisallowCall(true)
                        setRejectCall(true)
                    }

                    BlockType.NONE -> {
                        setDisallowCall(false)
                        setRejectCall(false)
                    }
                }
            }

            action.callLog?.also {
                if (action.block != null) {
                    setSkipCallLog(!it)
                }
            }

            action.notification?.also {
                if (action.block != null) {
                    setSkipNotification(!it)
                }
            }

            action.ring?.also {
                setSilenceCall(!it)
            }

        }.build()

        respondToCall(
            callDetails,
            callResponse,
        )
    }

    /**
     * Save the filter result to the filter log.
     * Blocks for a duration up to [timeout] before triing to finish in the background.
     *
     * @param call The call.
     * @param action The action taken.
     * @param timeout The timeout for blocking execution.
     */
    private fun saveFilterResultToLog(
        call: alt.foss.brunothg.tacl.bl.Call,
        action: FilterAction,
        timeout: Duration = 100.milliseconds,
    ) {
        if (
            action == FilterAction.NOTHING
            || action.callLog == false
        ) {
            return
        }

        TaclApplication.scope.launch {
            measureTime {
                val logEntry = FilterLogEntry(
                    call = call,
                    action = action,
                )
                val logLimit = appSettingsRepository.logMaxSize.first()

                filterLogRepository.saveAndPruneLogEntries(
                    logEntries = listOf(logEntry),
                    limit = logLimit
                )
                Log.d("TaclService", "saveFilterResultToLog: log written -> $logEntry")
            }.also { runTime ->
                Log.d("TaclService", "saveFilterResultToLog: finished in -> $runTime")
            }
        }.also { job ->
            runBlocking {
                val finished = withTimeoutOrNull(timeout) {
                    job.join()
                }.let { it != null }
                Log.d("TaclService", "saveFilterResultToLog: timeframe adhered -> $finished")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TaclService", "onDestroy")
    }

}
