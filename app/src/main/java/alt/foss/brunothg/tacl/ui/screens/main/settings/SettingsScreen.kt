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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.components.BackButton
import alt.foss.brunothg.tacl.ui.screens.main.Route
import alt.foss.brunothg.tacl.ui.screens.main.ScreenNavigation
import alt.foss.brunothg.tacl.ui.screens.main.buildPreviewScreenNavigation
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * Screen displaying the settings.
 *
 * @param navigation The screen navigation.
 */
@Composable
fun SettingsScreen(
    navigation: ScreenNavigation,
) {
    Scaffold(
        topBar = {
            SettingsTopBar(
                onBack = navigation.onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {

            item {
                CallScreeningSettingsItem()
            }

            item {
                CallRegionSettingsItem()
            }

            item {
                LibrariesItem(
                    onOpenLibraries = { navigation.onGoTo(Route.Settings.Libraries) },
                )
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                )
            }

            item {
                AppInfoSettingsItem()
            }

        }
    }

}

@Composable
private fun SettingsTopBar(
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.screen_settings_label))
        },
        navigationIcon = {
            if (onBack != null) {
                BackButton(
                    onBack = onBack,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun LibrariesItem(
    onOpenLibraries: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(stringResource(R.string.screen_settings_libraries_label))
        },
        supportingContent = {
            Text(stringResource(R.string.screen_settings_libraries_description))
        },
        modifier = Modifier.clickable {
            onOpenLibraries()
        }
    )
}

@PreviewLightDark
@Composable
fun PreviewSettingsScreen() {
    TaclPreview {
        SettingsScreen(
            navigation = buildPreviewScreenNavigation(),
        )
    }
}
