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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * Setup page displaying the EULA and license information.
 *
 * @param state The page UI state.
 */
@Composable
fun EulaPage(
    state: PageUiState,
) {
    val isPreview = LocalInspectionMode.current

    val resources = LocalResources.current
    val eulaScrollState = rememberScrollState()
    val eulaReadyState by remember {
        derivedStateOf {
            eulaScrollState.let { it.value >= it.maxValue }
        }
    }
    val licenseScrollState = rememberScrollState()
    val licenseText = remember {
        if (isPreview) {
            "License"
        } else {
            resources.openRawResource(
                R.raw.license
            ).bufferedReader().use { it.readText() }
        }
    }

    LaunchedEffect(state.isActive, eulaReadyState) {
        if (state.isActive) {
            state.onReady(eulaReadyState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.setup_page_eula_heading),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Column(
            modifier = Modifier
                .verticalScroll(eulaScrollState)
                .padding(
                    PaddingValues(horizontal = 16.dp)
                            + PaddingValues(top = 16.dp)
                )
                .fillMaxSize(),
        ) {
            Text(
                text = (stringResource(R.string.setup_page_eula_content)),
                fontFamily = FontFamily.Monospace,
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 20.dp),
            )
            Text(
                text = licenseText,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .horizontalScroll(licenseScrollState),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewEulaPage() {
    TaclPreview {
        EulaPage(
            state = buildPreviewPageState(),
        )
    }
}
