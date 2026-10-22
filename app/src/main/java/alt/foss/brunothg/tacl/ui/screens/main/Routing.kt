/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.screens.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import alt.foss.brunothg.tacl.BuildConfig
import alt.foss.brunothg.tacl.R
import kotlinx.serialization.Serializable

/**
 * Routes for navigation.
 */
sealed interface Route {

    @Serializable
    data object Tacl : Route

    @Serializable
    data object Debug : Route

    @Serializable
    data object Log : Route

    @Serializable
    data object Settings : Route {

        @Serializable
        data object MainSettings : Route {}

        @Serializable
        data object Libraries : Route

        val V_START = MainSettings
    }

    companion object {
        val V_START = Tacl
    }
}

/**
 * Navigation items for the navigation suite.
 *
 * @property route The route to navigate to.
 * @property labelRes The string resource for the label.
 * @property iconRes The drawable resource for the icon.
 * @property descriptionRes The string resource for the description.
 * @property enabled True if enabled.
 */
enum class NavItem(
    val route: Route,
    @StringRes val labelRes: Int? = null,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int? = null,
    val enabled: Boolean = true,
) {

    Tacl(
        route = Route.Tacl,
        labelRes = R.string.nav_tacl_label,
        descriptionRes = R.string.nav_tacl_description,
        iconRes = R.drawable.ic_m3_settings_phone,
    ),

    Debug(
        route = Route.Debug,
        labelRes = R.string.nav_debug_label,
        descriptionRes = R.string.nav_debug_description,
        iconRes = R.drawable.ic_m3_experiment,
        enabled = BuildConfig.DEBUG, // TODO rm if implemented
    ),

    Log(
        route = Route.Log,
        labelRes = R.string.nav_log_label,
        descriptionRes = R.string.nav_log_description,
        iconRes = R.drawable.ic_m3_call_log,
    ),

    Settings(
        route = Route.Settings,
        labelRes = R.string.nav_settings_label,
        descriptionRes = R.string.nav_settings_description,
        iconRes = R.drawable.ic_m3_settings,
    );

    companion object {
        val enabled = entries.filter { it.enabled }
    }
}

/**
 * Check if the destination is a top-level destination.
 */
fun NavDestination.isTopLevel(): Boolean {
    return NavItem.entries.any { navItem ->
        this.hasRoute(navItem.route::class)
                || (
                parent?.hasRoute(navItem.route::class) == true
                        && parent?.startDestinationId == id
                )
    }
}

/**
 * Navigation state for a screen.
 *
 * @property onGoTo Callback to navigate to a route.
 * @property onBack Optional callback to navigate back.
 */
data class ScreenNavigation(
    val onGoTo: (Route) -> Unit,
    val onBack: (() -> Unit)? = null,
)

/**
 * Remember the screen navigation.
 *
 * @param navController The nav controller.
 * @param navBackEntry The current nav back stack entry.
 * @return The screen navigation.
 */
@Composable
fun rememberScreenNavigation(
    navController: NavHostController,
    navBackEntry: NavBackStackEntry,
): ScreenNavigation {
    return remember(navController, navBackEntry) {
        val isTopLevel = navBackEntry.destination.isTopLevel()
        ScreenNavigation(
            onBack = if (!isTopLevel && navController.previousBackStackEntry != null) {
                { navController.popBackStack() }
            } else {
                null
            },
            onGoTo = { route -> navController.navigate(route) }
        )
    }
}

@Composable
fun buildPreviewScreenNavigation(): ScreenNavigation {
    return ScreenNavigation(
        onGoTo = {},
    )
}