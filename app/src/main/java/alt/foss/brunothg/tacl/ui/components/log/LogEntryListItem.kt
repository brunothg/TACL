/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.log

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.Call
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.bl.log.FilterLogEntry
import alt.foss.brunothg.tacl.ui.components.filter.FilterActionIndicator
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import java.net.URI

/**
 * A list item for a [FilterLogEntry].
 *
 * @param logEntry The log entry to display.
 */
@Composable
fun LogEntryListItem(
    logEntry: FilterLogEntry,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    context: Context = LocalContext.current,
) {
    val dateFormat = DateFormat.getDateFormat(context)
    val timeFormat = DateFormat.getTimeFormat(context)

    val timestamp = logEntry.timestamp.toEpochMilliseconds()
    val dateString = dateFormat.format(timestamp)
    val timeString = timeFormat.format(timestamp)
    val timestampString = "$dateString $timeString"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(padding),
    ) {
        Text(
            text = timestampString,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_m3_call),
                contentDescription = stringResource(R.string.misc_state_disabled),
                modifier = Modifier
                    .padding(end = 8.dp),
            )
            Text(
                text = logEntry.call.phoneNumber,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        FilterActionIndicator(
            action = logEntry.action,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewLogEntryListItem() {
    TaclPreview {
        LogEntryListItem(
            logEntry = FilterLogEntry(
                call = Call(
                    identifier = URI("tel:1234567890"),
                ),
                action = FilterAction(),
            )
        )
    }
}