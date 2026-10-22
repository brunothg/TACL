/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.misc

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp

/**
 * A list item for loading states.
 */
@Composable
fun LoadingListItem(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Text(
            text = stringResource(R.string.misc_state_loading),
        )
        CircularProgressIndicator()
    }
}

@PreviewLightDark
@Composable
fun PreviewLoadingListItem() {
    TaclPreview {
        LoadingListItem()
    }
}