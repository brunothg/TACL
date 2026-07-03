/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.filter.matchers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.filter.matchers.CallMatcher
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * UI state for the regex call matcher editor.
 *
 * @property regex The regex pattern string.
 */
@Immutable
data class RegexCallMatcherEditorUiState(
    val regex: String,
) : CallMatcherEditorUiState {

    override val isValid: Boolean = runCatching {
        regex.toRegex()
    }.isSuccess && regex.isNotBlank()

    override fun toDomain(): CallMatcher {
        return RegexCallMatcher(
            regex = regex.toRegex()
        )
    }

    companion object :
        CallMatcherEditorUiState.Factory<RegexCallMatcher, RegexCallMatcherEditorUiState> {

        override fun fromDomain(domain: RegexCallMatcher): RegexCallMatcherEditorUiState {
            return RegexCallMatcherEditorUiState(
                regex = domain.regex.pattern,
            )
        }

    }

}

/**
 * Editor for a [RegexCallMatcher].
 *
 * @param state The current UI state.
 * @param onChange Callback when the UI state changes.
 * @param modifier The modifier.
 */
@Composable
fun RegexCallMatcherEditor(
    state: RegexCallMatcherEditorUiState,
    onChange: (RegexCallMatcherEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {

        OutlinedTextField(
            value = state.regex,
            label = {
                Text(
                    text = stringResource(R.string.filter_editor_regex_matcher_regex_label)
                )
            },
            onValueChange = { newValue ->
                onChange(state.copy(regex = newValue))
            },
            modifier = Modifier
                .fillMaxWidth(),
            isError = !state.isValid,
            supportingText = if (!state.isValid) {
                {
                    Text(
                        text = stringResource(R.string.filter_editor_regex_matcher_error_invalid_regex)
                    )
                }
            } else {
                null
            },
        )

    }
}

@Composable
fun buildPreviewRegexCallMatcherEditorState(): RegexCallMatcherEditorUiState {
    return RegexCallMatcherEditorUiState.fromDomain(
        RegexCallMatcher(
            regex = ".*".toRegex()
        )
    )
}

@PreviewLightDark
@Composable
fun PreviewRegexCallMatcherEditor() {
    TaclPreview {
        RegexCallMatcherEditor(
            state = buildPreviewRegexCallMatcherEditorState(),
            onChange = {},
        )
    }
}