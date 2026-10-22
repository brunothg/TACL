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

import alt.foss.brunothg.tacl.ui.misc.UiTodo
import alt.foss.brunothg.tacl.ui.screens.main.settings.SettingsScreen
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation


/**
 * The main scaffold of the application containing navigation and main content.
 */
@Composable
fun MainScaffold() {
    val navController = rememberNavController()
    val navBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackEntry?.destination

    val onNavItem: (navItem: NavItem) -> Unit = { navItem ->
        Log.d("MainScaffold", "onNavItem($navItem)")
        navController.navigate(navItem.route) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
        }
    }

    NavigationSuiteScaffold(
        navigationItems = {
            NavItem.enabled.forEach { navItem ->
                val isActive = currentDestination?.hierarchy?.any {
                    it.hasRoute(navItem.route::class)
                } ?: false
                TaclNavigationSuiteItem(
                    selected = isActive,
                    navItem = navItem,
                    onClick = {
                        onNavItem(navItem)
                    },
                )
            }
        },
    ) {
        TaclNavHost(
            navController = navController,
        )
    }
}

@Composable
private fun TaclNavigationSuiteItem(
    navItem: NavItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationSuiteItem(
        label = {
            if (navItem.labelRes != null) {
                Text(stringResource(navItem.labelRes))
            }
        },
        icon = {
            Icon(
                painter = painterResource(navItem.iconRes),
                contentDescription = navItem.descriptionRes?.let { stringResource(it) },
            )
        },
        selected = selected,
        onClick = {
            if (!selected) onClick()
        },
    )
}

@Composable
private fun TaclNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Route.V_START,
    ) {
        composable<Route.Tacl> { navBackEntry ->
            TaclScreen(
                navigation = rememberScreenNavigation(navController, navBackEntry)
            )
        }

        composable<Route.Debug> {
            UiTodo("Debug route")
        }

        composable<Route.Log> { navBackEntry ->
            LogScreen(
                navigation = rememberScreenNavigation(navController, navBackEntry)
            )
        }

        navigation<Route.Settings>(
            startDestination = Route.Settings.V_START
        ) {
            composable<Route.Settings.MainSettings> { navBackEntry ->
                SettingsScreen(
                    navigation = rememberScreenNavigation(navController, navBackEntry)
                )
            }

            composable<Route.Settings.Libraries> { navBackEntry ->
                LibrariesScreen(
                    navigation = rememberScreenNavigation(navController, navBackEntry)
                )
            }
        }

    }
}

@PreviewLightDark
@Composable
fun PreviewMainScaffold() {
    TaclPreview {
        MainScaffold()
    }
}
