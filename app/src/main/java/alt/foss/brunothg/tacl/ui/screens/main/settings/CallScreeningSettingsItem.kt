/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.screens.main.settings

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.ui.components.permission.PermissionIndicator
import alt.foss.brunothg.tacl.ui.components.permission.rememberPermissionRequestLauncher
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * List item for the call screening role setting.
 */
@Composable
fun CallScreeningSettingsItem(
    modifier: Modifier = Modifier,
) {
    val callScreeningRole = AppPermissions.CallScreeningRole.definition
    val callScreeningRoleRequest = rememberPermissionRequestLauncher(
        permissionDefinition = callScreeningRole,
    )

    ListItem(
        headlineContent = {
            Text(stringResource(R.string.screen_settings_call_screening_role_label))
        },
        supportingContent = {
            Text(stringResource(R.string.screen_settings_call_screening_role_description))
        },
        trailingContent = {
            PermissionIndicator(
                permissionDefinitions = setOf(callScreeningRole)
            ) { granted ->
                val granted = granted ?: false

                Text(
                    text = stringResource(
                        when (granted) {
                            true -> R.string.misc_state_active
                            false -> R.string.misc_state_inactive
                        }
                    ),
                    color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier.clickable {
            callScreeningRoleRequest.invoke()
        }
    )
}

@PreviewLightDark
@Composable
fun PreviewCallScreeningSettingsItem() {
    TaclPreview {
        CallScreeningSettingsItem()
    }
}