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

import android.Manifest
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresPermission
import alt.foss.brunothg.tacl.TaclApplication
import alt.foss.brunothg.tacl.bl.filter.BlockType
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.filter.CallFilter.Companion.filterCall
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.di.TaclServiceEntryPoint
import alt.foss.brunothg.tacl.misc.measureFirstEmissionTime
import alt.foss.brunothg.tacl.service.CallUtils.toTaclCall
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
import kotlin.concurrent.Volatile
import kotlin.time.measureTime

/**
 * State of the call screening service.
 */
private data class TaclServiceState(
    val filters: List<CallFilter>,
    val region: String?,
)

/**
 * State cache of the call screening service.
 * Automatically fetches the [TaclServiceState] and caches them.
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
     * Repository for app settings.
     * Shortcut for [TaclServiceEntryPoint.appSettingsRepository].
     */
    private val appSettingsRepository
        get() = entryPoint.appSettingsRepository()

    private val stateCache: TaclServiceStateCache
        get() = TaclService.stateCache ?: synchronized(this) {
            TaclService.stateCache ?: run {
                Log.d("TaclService", "Initializing state cache for ${TaclApplication.callScreeningService}")
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
                FilterAction()
            Log.i("TaclService", "onScreenCall: $call -> $aclResult")

            respondToCall(
                callDetails,
                CallResponse.Builder().apply {

                    aclResult?.block?.also {
                        setDisallowCall(true)
                        when (it) {
                            BlockType.IGNORE -> {
                                setRejectCall(false)
                            }

                            BlockType.HANGUP -> {
                                setRejectCall(true)
                            }
                        }
                    }

                    aclResult?.callLog?.also {
                        if (aclResult.block != null) {
                            setSkipCallLog(!it)
                        }
                    }

                    aclResult?.notification?.also {
                        if (aclResult.block != null) {
                            setSkipNotification(!it)
                        }
                    }

                    aclResult?.ring?.also {
                        setSilenceCall(!it)
                    }

                }.build()
            )
        }.also { runTime ->
            Log.d("TaclService", "onScreenCall finished in $runTime")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TaclService", "onDestroy")
    }

}
