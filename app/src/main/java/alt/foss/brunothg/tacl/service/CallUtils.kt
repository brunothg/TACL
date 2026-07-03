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
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import alt.foss.brunothg.tacl.TaclApplication
import alt.foss.brunothg.tacl.bl.Call
import android.net.Uri
import java.net.URI
import java.util.Locale

/**
 * Utility class for calls.
 */
object CallUtils {

    /**
     * Convert [Uri] to [URI].
     */
    fun Uri.toURI(): URI = URI(this.toString())

    /**
     * Get the current sim region of the device.
     * Try to get the sim region from the
     * [SubscriptionManager] (permission [Manifest.permission.READ_PHONE_STATE] required)
     * first as it supports multi SIM setups.
     * Falls back to the [TelephonyManager] if the [SubscriptionManager] fails.
     * Does not throw any exceptions if permissions are missing, but null instead.
     *
     * @param context The [Context] of the application.
     * @return The current sim region of the device if available (ISO country code).
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getSimRegion(
        context: Context,
    ): String? {
        return runCatching {
            val subscriptionManager =
                ContextCompat.getSystemService(context, SubscriptionManager::class.java)
            val subscriptions = if (
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_SUBSCRIPTION)
                && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            )
                subscriptionManager?.activeSubscriptionInfoList?.toList()
            else
                null

            if (!subscriptions.isNullOrEmpty())
                subscriptions.first().countryIso
                    .let { if (it.isNullOrBlank()) null else it }
            else
                null
        }.getOrElse { ex ->
            Log.w(
                "Call",
                "getSimRegion - failed to get sim region of subscription manager",
                ex
            )
            null
        } ?: runCatching {
            val telephonyManager =
                ContextCompat.getSystemService(context, TelephonyManager::class.java)

            telephonyManager?.simCountryIso
                .let { if (it.isNullOrBlank()) null else it }
        }.getOrElse { ex ->
            Log.w(
                "Call",
                "getSimRegion - failed to get sim region of telephony manager",
                ex
            )
            null
        }
    }

    /**
     * Get the network region of the device.
     *
     * @param context The [Context] of the application.
     *
     * @return The network region of the device if available (ISO country code).
     */
    fun getNetworkRegion(
        context: Context,
    ): String? {
        return runCatching {
            val telephonyManager =
                ContextCompat.getSystemService(context, TelephonyManager::class.java)

            if (
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_RADIO_ACCESS)
            )
                telephonyManager?.networkCountryIso
                    .let { if (it.isNullOrBlank()) null else it }
            else
                null
        }.getOrElse { ex ->
            Log.w(
                "Call",
                "getNetworkRegion - failed to get network region of telephony manager",
                ex
            )
            null
        }
    }

    /**
     * Get the default call region of the device.
     * Tries to get the default call region from the SIM (requires [Manifest.permission.READ_PHONE_STATE] for multi SIM support).
     * Falls back to the network region and finally to the locale country.
     *
     * @param context The [Context] of the application.
     * @param locale The [Locale] to use as fallback.
     *
     * @return The default call region of the device.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDefaultCallRegion(
        context: Context,
        locale: Locale = Locale.getDefault(),
    ): String {
        return (
                getSimRegion(context = context)
                    ?: getNetworkRegion(context = context)
                    ?: locale.country
                ).uppercase()
    }

    /**
     * Convert [android.telecom.Call.Details] to [Call].
     * Requires [Manifest.permission.READ_PHONE_STATE] for safe multi SIM support (will not fail if missing and try it's best with fallbacks).
     *
     * @param context The [Context] of the application (fallback to applications context).
     * @param region The local region of the call (fallback to [getDefaultCallRegion] if missing).
     * @return The [Call].
     *
     * @see getDefaultCallRegion
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun android.telecom.Call.Details.toTaclCall(
        context: Context? = null,
        region: String? = null,
    ): Call {
        val context = context ?: TaclApplication.context
        val region = region ?: getDefaultCallRegion(context = context)
        val isEmergencyCall =
            (callProperties and android.telecom.Call.Details.PROPERTY_NETWORK_IDENTIFIED_EMERGENCY_CALL) != 0
                    || (callProperties and android.telecom.Call.Details.PROPERTY_EMERGENCY_CALLBACK_MODE) != 0
        val identifier = handle.toURI()

        return Call(
            identifier = identifier,
            region = region,
            isEmergencyCall = isEmergencyCall,
        )
    }

}