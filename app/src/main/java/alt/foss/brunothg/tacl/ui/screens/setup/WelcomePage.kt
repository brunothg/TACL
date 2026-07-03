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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * Setup page welcoming the user.
 *
 * @param state The page UI state.
 */
@Composable
fun WelcomePage(
    state: PageUiState,
) {

    LaunchedEffect(state.isActive) {
        if (state.isActive) {
            state.onReady(true)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
    ) {
        MainImage()

        Spacer(
            modifier = Modifier
                .height(40.dp),
        )

        Text(
            text = stringResource(R.string.setup_page_welcome_heading),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.setup_page_welcome_heading_sub),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(
            modifier = Modifier
                .height(20.dp),
        )

        Text(
            text = stringResource(R.string.setup_page_welcome_info),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun MainImage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(180.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxSize(),
            )
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .fillMaxSize(),
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_m3_rocket_launch),
                contentDescription = stringResource(R.string.misc_action_start),
                tint = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .size(32.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewWelcomePage() {
    TaclPreview {
        WelcomePage(
            state = buildPreviewPageState(),
        )
    }
}
