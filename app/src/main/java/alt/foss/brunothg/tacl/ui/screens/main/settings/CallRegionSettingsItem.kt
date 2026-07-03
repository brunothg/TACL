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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.misc.LoadState.Loading.valueOrNull
import alt.foss.brunothg.tacl.ui.components.PermissionIndicator
import alt.foss.brunothg.tacl.ui.components.SimpleDialog
import alt.foss.brunothg.tacl.ui.components.SimpleDialogButtons
import alt.foss.brunothg.tacl.ui.components.settings.CallRegionEditor
import alt.foss.brunothg.tacl.ui.components.settings.CallRegionEditorUiState
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.runtime.Immutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the call region settings.
 */
@Immutable
data class CallRegionSettingsUiState(
    val callRegion: LoadState<String?> = LoadState.Loading,
    val isEditorVisible: Boolean = false,
    val editorState: CallRegionEditorUiState = CallRegionEditorUiState(),
)

/**
 * ViewModel for call region settings.
 */
interface CallRegionSettingsViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<CallRegionSettingsUiState>

    /**
     * Set the editor state.
     */
    fun onEditorStateChange(state: CallRegionEditorUiState)

    /**
     * Set the dialog visibility.
     *
     * @param visible The new dialog visibility.
     */
    fun onDialogVisibilityChange(visible: Boolean)

    /**
     * Set the call region.
     *
     * @param region The new call region.
     */
    fun onCallRegionChange(region: String?)
}

/**
 * Hilt ViewModel for the Settings screen.
 *
 * @property appSettingsRepository The repository for app settings.
 */
@HiltViewModel
class HiltCallRegionSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel(), CallRegionSettingsViewModel {

    private val _editorState: MutableStateFlow<CallRegionEditorUiState> = MutableStateFlow(
        CallRegionEditorUiState()
    )

    private val _isDialogVisible: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    override val uiState: StateFlow<CallRegionSettingsUiState> = combine(
        appSettingsRepository.callRegion,
        _editorState,
        _isDialogVisible,
    ) { callRegion, editorState, isDialogVisible ->
        CallRegionSettingsUiState(
            callRegion = LoadState.Loaded(callRegion),
            editorState = editorState,
            isEditorVisible = isDialogVisible,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CallRegionSettingsUiState()
    )

    override fun onEditorStateChange(state: CallRegionEditorUiState) {
        _editorState.value = state
    }

    override fun onDialogVisibilityChange(visible: Boolean) {
        val currentVisiblility = _isDialogVisible.value

        if (currentVisiblility == visible) {
            return // Already in the desired state
        }

        if (visible) {
            _editorState.value = CallRegionEditorUiState(
                callRegion = uiState.value.callRegion.valueOrNull(),
            )
        }
        _isDialogVisible.value = visible
    }

    override fun onCallRegionChange(region: String?) {
        viewModelScope.launch {
            appSettingsRepository.setCallRegion(region)
        }
    }

}

/**
 * List item for the call region setting.
 */
@Composable
fun CallRegionSettingsItem(
    modifier: Modifier = Modifier,
    viewModel: CallRegionSettingsViewModel = hiltViewModel<HiltCallRegionSettingsViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val readPhoneStatePermission = AppPermissions.ReadPhoneStatePermission.definition
    // TODO request readPhoneStatePermission if set to auto detect

    ListItem(
        headlineContent = {
            Text(stringResource(R.string.screen_settings_call_region_label))
        },
        supportingContent = {
            Text(stringResource(R.string.screen_settings_call_region_description))
        },
        trailingContent = {
            uiState.callRegion.also { callRegionState ->
                when (callRegionState) {
                    is LoadState.Loading -> {
                        Text(
                            text = stringResource(R.string.misc_value_loading)
                        )
                    }

                    is LoadState.Loaded -> {
                        val callRegion = callRegionState.value

                        if (callRegion != null) {
                            Text(callRegion)
                        } else {
                            PermissionIndicator(
                                permissionDefinitions = setOf(readPhoneStatePermission)
                            ) { granted ->
                                val granted = granted ?: false

                                Text(
                                    stringResource(
                                        if (granted)
                                            R.string.screen_settings_call_region_auto_detect_label
                                        else
                                            R.string.screen_settings_call_region_auto_detect_permissionless_label
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier
            .clickable {
                viewModel.onDialogVisibilityChange(true)
            },
    )

    if (uiState.isEditorVisible) {
        CallRegionSettingsEditorDialog(
            state = uiState.editorState,
            onChange = {
                viewModel.onEditorStateChange(it)
            },
            onCancel = {
                viewModel.onDialogVisibilityChange(false)
            },
            onSave = {
                viewModel.onCallRegionChange(uiState.editorState.callRegion)
                viewModel.onDialogVisibilityChange(false)
            }
        )
    }
}

@Composable
private fun CallRegionSettingsEditorDialog(
    state: CallRegionEditorUiState,
    onChange: (CallRegionEditorUiState) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    val buttons: SimpleDialogButtons = listOf(
        {
            TextButton(
                onClick = onCancel,
            ) {
                Text(stringResource(R.string.misc_action_cancel))
            }
        },
        {
            TextButton(
                enabled = state.isValid,
                onClick = onSave,
            ) {
                Text(stringResource(R.string.misc_action_save))
            }
        },
    )

    SimpleDialog(
        onDismiss = onCancel,
        header = {
            Text(
                text = stringResource(R.string.screen_settings_call_region_label),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        buttons = buttons,
    ) {
        CallRegionEditor(
            state = state,
            onChange = onChange
        )
    }
}

@Composable
fun buildPreviewCallRegionSettingsViewModel(): CallRegionSettingsViewModel {
    return object : CallRegionSettingsViewModel {
        override val uiState: StateFlow<CallRegionSettingsUiState> = MutableStateFlow(
            CallRegionSettingsUiState()
        )

        override fun onDialogVisibilityChange(visible: Boolean) {
            error("Preview only")
        }

        override fun onEditorStateChange(state: CallRegionEditorUiState) {
            error("Preview only")
        }

        override fun onCallRegionChange(region: String?) {
            error("Preview only")
        }

    }
}

@PreviewLightDark
@Composable
fun PreviewCallRegionSettingsItem() {
    TaclPreview {
        CallRegionSettingsItem(
            viewModel = buildPreviewCallRegionSettingsViewModel(),
        )
    }
}