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

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import alt.foss.brunothg.tacl.misc.LoadState
import alt.foss.brunothg.tacl.misc.LoadState.Loading.valueOrNull
import alt.foss.brunothg.tacl.misc.NumberUtils.toHumanReadable
import alt.foss.brunothg.tacl.ui.components.misc.SimpleDialog
import alt.foss.brunothg.tacl.ui.components.misc.SimpleDialogButtons
import alt.foss.brunothg.tacl.ui.misc.PreviewUtils.withPreviewFallback
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the log max size settings.
 */
@Immutable
data class LogMaxSizeSettingsUiState(
    val logMaxSize: LoadState<Int> = LoadState.Loading,
    val isEditorVisible: Boolean = false,
    val editorValue: String = "",
) {
    /**
     * The editor value as an integer or null if not a valid integer.
     */
    val editorIntValue = editorValue.toIntOrNull() ?: -1

    /**
     * True if the entire state is valid.
     */
    val isValid: Boolean = runCatching {
        editorIntValue >= 0
    }.getOrNull() ?: false
}

/**
 * ViewModel for call region settings.
 */
interface LogMaxSizeSettingsViewModel {
    /**
     * The current UI state.
     */
    val uiState: StateFlow<LogMaxSizeSettingsUiState>

    /**
     * Set the editor value.
     */
    fun onEditorValueChange(value: String)

    /**
     * Set the dialog visibility.
     *
     * @param visible The new dialog visibility.
     */
    fun onDialogVisibilityChange(visible: Boolean)

    /**
     * Set the max log size.
     *
     * @param size the new max limit for log entries
     */
    fun onLogMaxSizeChange(size: Int)
}

/**
 * Hilt ViewModel for the Settings screen.
 *
 * @property appSettingsRepository The repository for app settings.
 */
@HiltViewModel
class HiltLogMaxSizeSettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel(), LogMaxSizeSettingsViewModel {

    private val _editorValue: MutableStateFlow<String> = MutableStateFlow(
        ""
    )

    private val _isDialogVisible: MutableStateFlow<Boolean> = MutableStateFlow(
        false
    )

    override val uiState: StateFlow<LogMaxSizeSettingsUiState> = combine(
        appSettingsRepository.logMaxSize,
        _isDialogVisible,
        _editorValue,
    ) { logMaxSize, isDialogVisible, editorValue ->
        LogMaxSizeSettingsUiState(
            logMaxSize = LoadState.Loaded(logMaxSize),
            isEditorVisible = isDialogVisible,
            editorValue = editorValue,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LogMaxSizeSettingsUiState()
    )

    override fun onEditorValueChange(value: String) {
        _editorValue.value = value
    }

    override fun onDialogVisibilityChange(visible: Boolean) {
        val currentVisibility = _isDialogVisible.value

        if (currentVisibility == visible) {
            return // Already in the desired state
        }

        if (visible) {
            _editorValue.value = uiState.value.logMaxSize.valueOrNull()?.toString() ?: ""
        }
        _isDialogVisible.value = visible
    }

    override fun onLogMaxSizeChange(size: Int) {
        viewModelScope.launch {
            appSettingsRepository.setLogMaxSize(size)
        }
    }

}

/**
 * List item for the log max size setting.
 */
@Composable
fun LogMaxSizeSettingsItem(
    modifier: Modifier = Modifier,
    viewModel: LogMaxSizeSettingsViewModel = withPreviewFallback(
        { hiltViewModel<HiltLogMaxSizeSettingsViewModel>() },
        { buildPreviewLogMaxSizeSettingsViewModel() }
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListItem(
        headlineContent = {
            Text(stringResource(R.string.screen_settings_log_max_size_label))
        },
        supportingContent = {
            Text(stringResource(R.string.screen_settings_log_max_size_description))
        },
        trailingContent = {
            uiState.logMaxSize.also { logMaxSizeState ->
                when (logMaxSizeState) {
                    is LoadState.Loading -> {
                        Text(
                            text = stringResource(R.string.misc_state_loading)
                        )
                    }

                    is LoadState.Loaded -> {
                        val logMaxSize = logMaxSizeState.value
                        Text(logMaxSize.toHumanReadable())
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
        LogMaxSizeSettingsEditorDialog(
            state = uiState,
            onChange = {
                viewModel.onEditorValueChange(it)
            },
            onCancel = {
                viewModel.onDialogVisibilityChange(false)
            },
            onSave = {
                viewModel.onLogMaxSizeChange(uiState.editorIntValue)
                viewModel.onDialogVisibilityChange(false)
            }
        )
    }
}

@Composable
private fun LogMaxSizeSettingsEditorDialog(
    state: LogMaxSizeSettingsUiState,
    onChange: (String) -> Unit,
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
                text = stringResource(R.string.screen_settings_log_max_size_label),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        buttons = buttons,
    ) {
        TextField(
            value = state.editorValue,
            onValueChange = { value ->
                onChange(value)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Composable
fun buildPreviewLogMaxSizeSettingsViewModel(): LogMaxSizeSettingsViewModel {
    return object : LogMaxSizeSettingsViewModel {
        override val uiState: StateFlow<LogMaxSizeSettingsUiState> = MutableStateFlow(
            LogMaxSizeSettingsUiState()
        )

        override fun onEditorValueChange(value: String) {
            error("Preview only")
        }

        override fun onDialogVisibilityChange(visible: Boolean) {
            error("Preview only")
        }

        override fun onLogMaxSizeChange(size: Int) {
            error("Preview only")
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLogMaxSizeSettingsItem() {
    TaclPreview {
        LogMaxSizeSettingsItem()
    }
}