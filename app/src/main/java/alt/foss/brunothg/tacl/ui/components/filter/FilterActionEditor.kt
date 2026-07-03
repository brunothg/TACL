/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.filter.BlockType
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.ui.components.filter.FilterActionEditorUiState.Companion.toUiState
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * UI state for the filter action editor.
 *
 * @property block The block type.
 * @property ring True if ringing.
 * @property notification True if notifying.
 * @property callLog True if logging.
 */
@Immutable
data class FilterActionEditorUiState(
    val block: BlockType?,
    val ring: Boolean?,
    val notification: Boolean?,
    val callLog: Boolean?,
) {

    /**
     * True if the state is valid.
     */
    val isValid: Boolean = true

    /**
     * Map this UI state to the domain model [FilterAction].
     *
     * @return The [FilterAction].
     */
    fun toDomain(): FilterAction {
        return FilterAction(
            block = block,
            ring = ring,
            notification = notification,
            callLog = callLog,
        )
    }

    companion object {
        /**
         * Map [FilterAction] to [FilterActionEditorUiState].
         *
         * @return The UI state.
         */
        fun FilterAction.toUiState(): FilterActionEditorUiState {
            return FilterActionEditorUiState(
                block = block,
                ring = ring,
                notification = notification,
                callLog = callLog,
            )
        }
    }
}

/**
 * Editor for a [FilterAction].
 *
 * @param state The current UI state.
 * @param onChange Callback when the UI state changes.
 * @param modifier The modifier.
 */
@Composable
fun FilterActionEditor(
    state: FilterActionEditorUiState,
    onChange: (FilterActionEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        BlockTypeSelection(
            value = state.block,
            label = {
                Text(
                    text = stringResource(R.string.filter_editor_action_block_label),
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            onValueChange = { newValue ->
                onChange(state.copy(block = newValue))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.filter_editor_action_ring_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(vertical = 8.dp),
        )
        ActionOptionSelection(
            value = state.ring,
            onValueChange = { newValue ->
                onChange(state.copy(ring = newValue))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.filter_editor_action_notification_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(vertical = 8.dp),
        )
        ActionOptionSelection(
            value = state.notification,
            onValueChange = { newValue ->
                onChange(state.copy(notification = newValue))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
        Text(
            text = stringResource(R.string.filter_editor_action_call_log_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(vertical = 8.dp),
        )
        ActionOptionSelection(
            value = state.callLog,
            onValueChange = { newValue ->
                onChange(state.copy(callLog = newValue))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
fun BlockType.getLabel(): String {
    return when (this) {
        BlockType.IGNORE -> stringResource(R.string.filter_editor_action_block_ignore_label)
        BlockType.HANGUP -> stringResource(R.string.filter_editor_action_block_hangup_label)
    }
}

@Composable
private fun BlockTypeSelection(
    value: BlockType?,
    onValueChange: (BlockType?) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        stringResource(R.string.misc_value_default) to null,
    ) + BlockType.entries
        .map {
            it.getLabel() to it
        }
        .sortedBy { it.first }

    val selectedOption = options.find { it.second == value }
        ?: (stringResource(R.string.misc_value_unknown) to value)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expansionState ->
            expanded = expansionState
        },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedOption.first,
            label = label,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true,
                )
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            options.forEach { (label, option) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                        )
                    },
                    onClick = {
                        expanded = false
                        onValueChange(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionOptionSelection(
    value: Boolean?,
    onValueChange: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        stringResource(R.string.misc_state_default) to null,
        stringResource(R.string.misc_state_on) to true,
        stringResource(R.string.misc_state_off) to false,
    )

    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        options.forEachIndexed { index, (label, option) ->
            SegmentedButton(
                selected = value == option,
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    onValueChange(option)
                },
            ) {
                Text(
                    text = label,
                )
            }
        }
    }
}

@Composable
fun buildPreviewFilterActionEditorState(): FilterActionEditorUiState {
    return FilterAction().toUiState()
}

@PreviewLightDark
@Composable
fun PreviewFilterActionEditor() {
    TaclPreview {
        FilterActionEditor(
            state = buildPreviewFilterActionEditorState(),
            onChange = {},
        )
    }
}