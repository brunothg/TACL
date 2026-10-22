/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.permission

import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.bl.permission.PermissionDefinition
import alt.foss.brunothg.tacl.bl.permission.PermissionManager
import alt.foss.brunothg.tacl.bl.permission.PermissionState
import alt.foss.brunothg.tacl.bl.permission.PermissionType
import alt.foss.brunothg.tacl.ui.misc.PreviewUtils.withPreviewFallback
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.app.Activity.RESULT_OK
import android.app.role.RoleManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

/**
 * ViewModel for permissions.
 */
interface PermissionsViewModel {

    /**
     * All permissions and their states.
     */
    val permissions: StateFlow<Set<PermissionState>>

    /**
     * Get a flow of states for the specified definitions.
     *
     * @param definitions The definitions to filter for.
     * @return A flow of the permission states.
     */
    fun permissions(
        definitions: Collection<PermissionDefinition>
    ): Flow<Set<PermissionState>> {
        return permissions
            .map { states ->
                states
                    .filter { state ->
                        definitions.contains(state.definition)
                    }
                    .toSet()
            }
            .distinctUntilChanged()
    }

    /**
     * Get a flow of state for a specific definition.
     *
     * @param definition The definition to filter for.
     * @return A flow of the permission state.
     */
    fun permission(
        definition: PermissionDefinition
    ): Flow<PermissionState> {
        return permissions
            .mapNotNull { states ->
                states
                    .firstOrNull { state ->
                        state.definition == definition
                    }
            }
            .distinctUntilChanged()
    }
}

/**
 * Hilt ViewModel for permissions.
 */
@HiltViewModel
class HiltPermissionsViewModel @Inject constructor(
    permissionManager: PermissionManager
) : ViewModel(), PermissionsViewModel {

    override val permissions = permissionManager.state

}

/**
 * Composable that displays content based on whether permissions are granted.
 *
 * @param permissionDefinitions The permissions to check.
 * @param content The content to display.
 */
@Composable
fun PermissionIndicator(
    permissionDefinitions: Collection<PermissionDefinition>,
    content: @Composable (granted: Boolean?) -> Unit,
) {

    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        return content(false)
    }

    val permissionsViewModel: PermissionsViewModel = withPreviewFallback(
        { hiltViewModel<HiltPermissionsViewModel>() },
        { buildPreviewPermissionsViewModel() }
    )
    val hasRoles by remember {
        permissionsViewModel.permissions(permissionDefinitions)
            .map { states ->
                Log.d("PermissionIndicator", "States: $states")
                states.map { state -> state.granted }
                    .fold(true) { acc, cur -> acc && cur }
            }
    }.collectAsStateWithLifecycle(null)


    content(hasRoles)
}

/**
 * Remember a launcher for requesting a permission.
 *
 * @param permissionDefinition The permission to request.
 * @param resultHandler Optional callback for the result.
 * @return A callback to launch the request.
 */
@Composable
fun rememberPermissionRequestLauncher(
    permissionDefinition: PermissionDefinition,
    resultHandler: ((success: Boolean, result: ActivityResult) -> Unit)? = null,
): () -> Unit {
    return when (permissionDefinition.type) {
        PermissionType.Role -> rememberRoleRequestLauncher(
            permissionDefinition = permissionDefinition,
            resultHandler = resultHandler,
        )

        PermissionType.RuntimePermission -> TODO("Runtime permission request launcher")
    }
}

/**
 * Remember a launcher for requesting a role.
 *
 * @param permissionDefinition The role permission to request.
 * @param resultHandler Optional callback for the result.
 * @return A callback to launch the request.
 */
@Composable
fun rememberRoleRequestLauncher(
    permissionDefinition: PermissionDefinition,
    resultHandler: ((success: Boolean, result: ActivityResult) -> Unit)? = null,
): () -> Unit {
    require(permissionDefinition.type == PermissionType.Role) {
        "Only role permissions allowed but ${permissionDefinition.type} provided."
    }
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        return {
            Log.d("PermissionsUi", "Preview role request")
        }
    }

    val context = LocalContext.current
    val roleManager = context.getSystemService(RoleManager::class.java)
    val launchDeltaSuccessTime = 150L
    var launchTime by remember { mutableLongStateOf(0L) }


    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val success = result.resultCode == RESULT_OK
        val elapsedTime = System.currentTimeMillis() - launchTime
        Log.d("PermissionsUi", "fallbackLauncher success: $success, elapsedTime: $elapsedTime")

        resultHandler?.invoke(elapsedTime > launchDeltaSuccessTime, result)
    }
    val settingsAction: (() -> Unit)? = if (permissionDefinition.settingsIntentBuilder != null) ({
        val action = permissionDefinition.settingsIntentBuilder()
        Log.d("PermissionsUi", "Request call screening role through fallback action: $action")

        settingsLauncher.launch(action)
    }) else null

    val activityLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val success = result.resultCode == RESULT_OK
        val elapsedTime = System.currentTimeMillis() - launchTime
        Log.d("PermissionsUi", "activityLauncher success: $success, elapsedTime: $elapsedTime")

        val returnValue: Boolean?
        if (elapsedTime < launchDeltaSuccessTime) {
            if (settingsAction != null) {
                settingsAction()
                returnValue = null
            } else {
                returnValue = false
            }
        } else {
            returnValue = true
        }

        if (returnValue != null) {
            resultHandler?.invoke(returnValue, result)
        }
    }
    val roleManagerAction: () -> Unit = {
        Log.d("PermissionsUi", "Request call screening role")
        roleManager.createRequestRoleIntent(permissionDefinition.name).also { intent ->
            activityLauncher.launch(intent)
        }
    }

    return {
        launchTime = System.currentTimeMillis()

        if (
            roleManager.isRoleAvailable(permissionDefinition.name)
            && !roleManager.isRoleHeld(permissionDefinition.name)
        ) {
            roleManagerAction()
        } else if (settingsAction != null) {
            settingsAction()
        }
    }
}

@Composable
fun buildPreviewPermissionsViewModel(): PermissionsViewModel {
    return object : PermissionsViewModel {
        override val permissions: StateFlow<Set<PermissionState>> = MutableStateFlow(
            emptySet()
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewSetupScaffold() {
    TaclPreview {
        PermissionIndicator(
            permissionDefinitions = setOf(AppPermissions.ReadPhoneStatePermission.definition)
        ) { granted ->
            Text("Granted: $granted")
        }
    }
}