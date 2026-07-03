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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * UI state for a setup page.
 *
 * @property isActive True if the page is currently active.
 * @property onReady Callback to signal that the page is done and can proceed.
 */
@Stable
class PageUiState (
    val isActive: Boolean,
    val onReady: (ready: Boolean) -> Unit,
)

/**
 * Definition of a setup page.
 *
 * @property version The version of the setup page.
 * @property content The composable content.
 */
@Immutable
data class SetupPage(
    val version: Int = 1,
    val content: (@Composable (
        state: PageUiState,
    ) -> Unit)
)

/**
 * The list of all setup pages.
 */
val SetupPages: List<SetupPage> = listOf(
    SetupPage { state ->
        WelcomePage(
            state = state,
        )
    },
    SetupPage { state ->
        EulaPage(
            state = state,
        )
    },
    SetupPage { state ->
        PrivacyPolicyPage(
            state = state,
        )
    },
    SetupPage { state ->
        PermissionsPage(
            state = state,
        )
    },
)

@Composable
fun buildPreviewPageState(): PageUiState {
    return PageUiState(
        isActive = true,
        onReady = {},
    )
}