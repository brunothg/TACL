/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.filter.BlockType
import alt.foss.brunothg.tacl.bl.filter.CallFilter
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.foundation.layout.PaddingValues

/**
 * A list item for a [CallFilter].
 *
 * @param callFilter The call filter to display.
 */
@Composable
fun CallFilterListItem(
    callFilter: CallFilter,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = callFilter.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f),
            )
            if (!callFilter.enabled) {
                Icon(
                    painter = painterResource(R.drawable.ic_m3_block),
                    contentDescription = stringResource(R.string.misc_state_disabled),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(start = 8.dp),
                )
            }
        }
        Text(
            text = callFilter.description ?: "",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        FilterActionIndicator(
            action = callFilter.action,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewCallFilterListItem() {
    TaclPreview {
        CallFilterListItem(
            callFilter = CallFilter(
                orderIndex = 1,
                name = "Call filter 1",
                description = "Description of call filter 1",
                matcher = RegexCallMatcher(".*".toRegex()),
                action = FilterAction(),
            )
        )
    }
}