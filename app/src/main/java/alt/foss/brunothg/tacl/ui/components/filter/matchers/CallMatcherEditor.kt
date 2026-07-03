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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import alt.foss.brunothg.tacl.bl.filter.matchers.CallMatcher
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher

/**
 * UI state for a call matcher editor.
 */
sealed interface CallMatcherEditorUiState {

    /**
     * True if the state is valid.
     */
    val isValid: Boolean

    /**
     * Map this UI state to the domain model [CallMatcher].
     *
     * @return The [CallMatcher].
     */
    fun toDomain(): CallMatcher

    /**
     * Factory for creating UI state from domain model.
     */
    fun interface Factory<M : CallMatcher, S : CallMatcherEditorUiState> {
        /**
         * Create UI state from domain model.
         */
        fun fromDomain(domain: M): S
    }

    companion object {

        /**
         * Map [CallMatcher] to [CallMatcherEditorUiState].
         *
         * @return The UI state.
         */
        fun CallMatcher.toUiState(): CallMatcherEditorUiState {
            return when (this) {
                is RegexCallMatcher -> RegexCallMatcherEditorUiState.fromDomain(this)
            }
        }

    }
}

/**
 * Editor for a [CallMatcher].
 *
 * @param state The current UI state.
 * @param onChange Callback when the UI state changes.
 * @param modifier The modifier.
 */
@Composable
fun CallMatcherEditor(
    state: CallMatcherEditorUiState,
    onChange: (CallMatcherEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is RegexCallMatcherEditorUiState -> RegexCallMatcherEditor(
            state = state,
            onChange = onChange,
            modifier = modifier,
        )
    }
}