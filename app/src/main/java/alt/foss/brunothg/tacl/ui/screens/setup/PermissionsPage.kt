/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.screens.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.ui.components.PermissionIndicator
import alt.foss.brunothg.tacl.ui.components.rememberPermissionRequestLauncher
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * Setup page requesting required permissions.
 *
 * @param state The page UI state.
 */
@Composable
fun PermissionsPage(
    state: PageUiState,
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(state.isActive) {
        if (state.isActive) {
            state.onReady(true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.setup_page_permissions_heading),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Column(
            modifier = Modifier
                .padding(
                    PaddingValues(horizontal = 16.dp)
                            + PaddingValues(top = 16.dp)
                )
                .verticalScroll(scrollState)
                .fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.setup_page_permissions_intro)
            )
            Spacer(modifier = Modifier.padding(15.dp))
            CallScreeningServiceRequest()
            // TODO maybe request READ_PHONE_STATE permission at setup stage
        }
    }
}

@Composable
private fun CallScreeningServiceRequest() {
    val isPreview = LocalInspectionMode.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.setup_page_permissions_request_call_screening_service),
            modifier = Modifier
                .padding(end = 10.dp),
        )

        if (isPreview) {
            Switch(
                checked = false,
                onCheckedChange = {},
            )
        } else {
            val callScreeningRole = AppPermissions.CallScreeningRole.definition
            val callScreeningRoleRequest = rememberPermissionRequestLauncher(
                permissionDefinition = callScreeningRole,
            )

            PermissionIndicator(
                permissionDefinitions = setOf(callScreeningRole)
            ) { granted ->
                val granted = granted ?: false

                Switch(
                    checked = granted,
                    onCheckedChange = {
                        callScreeningRoleRequest.invoke()
                    },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewPermissionsPage() {
    TaclPreview {
        PermissionsPage(
            state = buildPreviewPageState(),
        )
    }
}
