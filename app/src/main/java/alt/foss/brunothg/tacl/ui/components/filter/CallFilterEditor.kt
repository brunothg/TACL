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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher
import alt.foss.brunothg.tacl.misc.ID
import alt.foss.brunothg.tacl.ui.components.filter.CallFilterEditorUiState.Companion.toUiState
import alt.foss.brunothg.tacl.ui.components.filter.FilterActionEditorUiState.Companion.toUiState
import alt.foss.brunothg.tacl.ui.components.filter.matchers.CallMatcherEditor
import alt.foss.brunothg.tacl.ui.components.filter.matchers.CallMatcherEditorUiState
import alt.foss.brunothg.tacl.ui.components.filter.matchers.CallMatcherEditorUiState.Companion.toUiState
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * UI state for the call filter editor.
 *
 * @property id The unique identifier of the filter.
 * @property name The name of the filter.
 * @property description The description of the filter.
 * @property orderIndex The order index.
 * @property enabled True if enabled.
 * @property invertMatch True if match should be inverted.
 * @property action The UI state for the filter action editor.
 * @property matcher The UI state for the call matcher editor.
 */
@Immutable
data class CallFilterEditorUiState(
    val id: ID,
    val name: String,
    val description: String,
    val orderIndex: Int?,
    val enabled: Boolean,
    val invertMatch: Boolean,
    val action: FilterActionEditorUiState,
    val matcher: CallMatcherEditorUiState,
) {

    /**
     * True if the name is valid.
     */
    val isNameValid: Boolean = name.isNotBlank()

    /**
     * True if the order index is valid.
     */
    val isOrderIndexValid: Boolean = orderIndex != null

    /**
     * True if the entire state is valid.
     */
    val isValid: Boolean = runCatching {
        isNameValid
                && isOrderIndexValid
                && matcher.isValid
                && action.isValid
    }.getOrNull() ?: false

    /**
     * Map this UI state to the domain model [CallFilter].
     *
     * @return The [CallFilter].
     */
    fun toDomain(): CallFilter {
        return CallFilter(
            id = id,
            name = name.trim(),
            description = description.trim(),
            enabled = enabled,
            orderIndex = orderIndex ?: Int.MAX_VALUE,
            invertMatch = invertMatch,
            matcher = matcher.toDomain(),
            action = action.toDomain(),
        )
    }

    companion object {

        /**
         * Build a default [CallFilterEditorUiState].
         *
         * @return The UI state.
         */
        fun buildDefault(): CallFilterEditorUiState {
            return CallFilter(
                orderIndex = Int.MAX_VALUE,
                name = "",
                matcher = RegexCallMatcher(".*".toRegex()),
                action = FilterAction(),
            ).toUiState()
        }

        /**
         * Map [CallFilter] to [CallFilterEditorUiState].
         *
         * @return The UI state.
         */
        fun CallFilter.toUiState(): CallFilterEditorUiState {
            return CallFilterEditorUiState(
                id = id,
                name = name,
                description = description ?: "",
                orderIndex = orderIndex,
                enabled = enabled,
                invertMatch = invertMatch,
                matcher = matcher.toUiState(),
                action = action.toUiState(),
            )
        }
    }

}

/**
 * Editor for a [CallFilter].
 *
 * @param state The current UI state.
 * @param onChange Callback when the UI state changes.
 * @param modifier The modifier.
 */
@Composable
fun CallFilterEditor(
    state: CallFilterEditorUiState,
    onChange: (CallFilterEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .focusable(),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.filter_editor_title),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f),
            )

            Text(
                text = stringResource(R.string.filter_editor_enable_disable_switch_label)
            )
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = state.enabled,
                onCheckedChange = { newEnabled ->
                    onChange(state.copy(enabled = newEnabled))
                }
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.name,
            label = {
                Text(
                    text = stringResource(R.string.filter_editor_name_label)
                )
            },
            singleLine = true,
            maxLines = 1,
            isError = !state.isNameValid,
            onValueChange = { newName ->
                onChange(state.copy(name = newName))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            singleLine = false,
            value = state.description,
            label = {
                Text(
                    text = stringResource(R.string.filter_editor_description_label)
                )
            },
            onValueChange = { newDescription ->
                onChange(state.copy(description = newDescription))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        OrderIndexInput(
            value = state.orderIndex,
            label = {
                Text(
                    text = stringResource(R.string.filter_editor_order_index_label)
                )
            },
            onValueChange = { newOrderIndex ->
                onChange(state.copy(orderIndex = newOrderIndex))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.filter_editor_action_section_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(vertical = 8.dp),
        )
        FilterActionEditor(
            state = state.action,
            onChange = { newAction ->
                onChange(state.copy(action = newAction))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.filter_editor_matcher_section_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(vertical = 8.dp),
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = stringResource(R.string.filter_editor_matcher_invert_label)
            )
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = state.invertMatch,
                onCheckedChange = { newInvertMatch ->
                    onChange(state.copy(invertMatch = newInvertMatch))
                }
            )
        }
        CallMatcherEditor(
            // TODO selectable matcher type
            state = state.matcher,
            onChange = { newMatcher ->
                onChange(state.copy(matcher = newMatcher))
            },
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun OrderIndexInput(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
) {
    val localDensity = LocalDensity.current
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val options = listOf(
        stringResource(R.string.filter_editor_order_index_last_label) to Int.MAX_VALUE,
        stringResource(R.string.filter_editor_order_index_first_label) to Int.MIN_VALUE,
    )
    val selectedOption = options.find { it.second == value }
        ?: ("${value ?: ""}" to value)
    var currentIntValue by remember(
        selectedOption.second
    ) { mutableStateOf(selectedOption.second) }
    var currentStringValue by remember(
        selectedOption.first
    ) { mutableStateOf(selectedOption.first) }

    Column(
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = currentStringValue,
            label = label,
            singleLine = true,
            maxLines = 1,
            onValueChange = { newValue ->
                val newIntValue = newValue.toIntOrNull()

                currentStringValue = newValue
                currentIntValue = newIntValue
                onValueChange(newIntValue)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier
                        .clickable {
                            expanded = !expanded
                        },
                )
            },
            isError = currentIntValue == null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .width(with(localDensity) { textFieldSize.width.toDp() }),
        ) {
            options.forEach { (label, option) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label
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
fun buildPreviewCallFilterEditorState(): CallFilterEditorUiState {
    return CallFilter(
        name = "Test filter",
        description = "Test filter description",
        orderIndex = 0,
        matcher = RegexCallMatcher(
            regex = ".*".toRegex()
        ),
        action = FilterAction(),
    ).toUiState()
}

@PreviewLightDark
@Composable
fun PreviewCallFilterEditor() {
    TaclPreview {
        CallFilterEditor(
            state = buildPreviewCallFilterEditorState(),
            onChange = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        )
    }
}