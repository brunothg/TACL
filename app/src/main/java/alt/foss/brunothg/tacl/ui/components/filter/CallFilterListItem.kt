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

@Composable
private fun statusColor(status: Any?): Color {
    return when (status) {
        null -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.primary
    }
}

/**
 * A list item for a [CallFilter].
 *
 * @param callFilter The call filter to display.
 * @param modifier The modifier.
 */
@Composable
fun CallFilterListItem(
    callFilter: CallFilter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BlockActionIcon(callFilter)
            RingActionIcon(callFilter)
            NotificationActionIcon(callFilter)
            CallLogActionIcon(callFilter)
        }
    }
}

@Composable
private fun CallLogActionIcon(callFilter: CallFilter) {
    Icon(
        painter = painterResource(
            when (callFilter.action.callLog) {
                true -> R.drawable.ic_m3_chat_bubble
                false -> R.drawable.ic_m3_chat_bubble_off
                null -> R.drawable.ic_m3_chat_dashed
            }
        ),
        contentDescription = stringResource(
            when (callFilter.action.callLog) {
                true -> R.string.filter_action_call_log_on_description
                false -> R.string.filter_action_call_log_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(callFilter.action.callLog),
    )
}

@Composable
private fun NotificationActionIcon(callFilter: CallFilter) {
    Icon(
        painter = painterResource(
            when (callFilter.action.notification) {
                true -> R.drawable.ic_m3_notifications
                false -> R.drawable.ic_m3_notifications_off
                null -> R.drawable.ic_m3_notifications
            }
        ),
        contentDescription = stringResource(
            when (callFilter.action.notification) {
                true -> R.string.filter_action_notification_on_description
                false -> R.string.filter_action_notification_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(callFilter.action.notification),
    )
}

@Composable
private fun RingActionIcon(callFilter: CallFilter) {
    Icon(
        painter = painterResource(
            when (callFilter.action.ring) {
                true -> R.drawable.ic_m3_mobile_sound
                false -> R.drawable.ic_m3_mobile_sound_off
                null -> R.drawable.ic_m3_mobile
            }
        ),
        contentDescription = stringResource(
            when (callFilter.action.ring) {
                true -> R.string.filter_action_ring_on_description
                false -> R.string.filter_action_ring_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(callFilter.action.ring),
    )
}

@Composable
private fun BlockActionIcon(callFilter: CallFilter) {
    Icon(
        painter = painterResource(
            when (callFilter.action.block) {
                BlockType.HANGUP -> R.drawable.ic_m3_phone_cancel
                BlockType.IGNORE -> R.drawable.ic_m3_phone_missed
                null -> R.drawable.ic_m3_phone_enabled
            }
        ),
        contentDescription = stringResource(
            when (callFilter.action.block) {
                BlockType.HANGUP -> R.string.filter_action_block_hangup_description
                BlockType.IGNORE -> R.string.filter_action_block_ignore_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(callFilter.action.block)
    )
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