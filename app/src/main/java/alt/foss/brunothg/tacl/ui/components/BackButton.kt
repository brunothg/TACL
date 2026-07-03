/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * A button for navigating back.
 *
 * @param onBack Callback when the button is clicked.
 */
@Composable
fun BackButton(
    onBack: () -> Unit
) {
    IconButton(
        onClick = onBack
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_m3_arrow_back),
            contentDescription = stringResource(R.string.misc_action_back),
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewBackButton() {
    TaclPreview {
        BackButton(
            onBack = {}
        )
    }
}
