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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.ui.components.misc.AppInfoSection
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

@Composable
fun AppInfoSettingsItem(
    modifier: Modifier = Modifier,
) {
    AppInfoSection(
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
fun PreviewAppInfoSettingsItem() {
    TaclPreview {
        AppInfoSettingsItem()
    }
}