/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.permission

import android.Manifest
import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import alt.foss.brunothg.tacl.BuildConfig

/**
 * Type of permission.
 */
enum class PermissionType {
    /**
     * An Android Role (e.g. Call Screening).
     */
    Role,

    /**
     * A standard Android Runtime Permission.
     */
    RuntimePermission,
}

/**
 * Definition of a permission.
 *
 * @property name Name/ID of the permission as used by Android system itself.
 * @property type The type of the permission.
 * @property settingsIntentBuilder Optional builder for an intent to open the settings for this permission.
 */
data class PermissionDefinition(
    val name: String,
    val type: PermissionType,
    val settingsIntentBuilder: (() -> Intent)? = {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        }
    },
)

/**
 * Permissions used and defined by the app.
 *
 * @property definition The permission definition.
 */
enum class AppPermissions(
    val definition: PermissionDefinition
) {
    CallScreeningRole(
        PermissionDefinition(
        name = RoleManager.ROLE_CALL_SCREENING,
        type = PermissionType.Role,
        settingsIntentBuilder = {
            Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        }
    )),

    // READ_PHONE_STATE
    ReadPhoneStatePermission(
        PermissionDefinition(
            name = Manifest.permission.READ_PHONE_STATE,
            type = PermissionType.RuntimePermission,
        )
    ),
}