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

import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.bl.filter.BlockType
import alt.foss.brunothg.tacl.bl.filter.FilterAction
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark

/**
 * A visual icon indicator for a [FilterAction].
 */
@Composable
fun FilterActionIndicator(
    action: FilterAction,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BlockActionIcon(action)
        RingActionIcon(action)
        NotificationActionIcon(action)
        CallLogActionIcon(action)
    }
}

@Composable
private fun CallLogActionIcon(action: FilterAction) {
    Icon(
        painter = painterResource(
            when (action.callLog) {
                true -> R.drawable.ic_m3_chat_bubble
                false -> R.drawable.ic_m3_chat_bubble_off
                null -> R.drawable.ic_m3_chat_dashed
            }
        ),
        contentDescription = stringResource(
            when (action.callLog) {
                true -> R.string.filter_action_call_log_on_description
                false -> R.string.filter_action_call_log_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(action.callLog),
    )
}

@Composable
private fun NotificationActionIcon(action: FilterAction) {
    Icon(
        painter = painterResource(
            when (action.notification) {
                true -> R.drawable.ic_m3_notifications
                false -> R.drawable.ic_m3_notifications_off
                null -> R.drawable.ic_m3_notifications
            }
        ),
        contentDescription = stringResource(
            when (action.notification) {
                true -> R.string.filter_action_notification_on_description
                false -> R.string.filter_action_notification_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(action.notification),
    )
}

@Composable
private fun RingActionIcon(action: FilterAction) {
    Icon(
        painter = painterResource(
            when (action.ring) {
                true -> R.drawable.ic_m3_mobile_sound
                false -> R.drawable.ic_m3_mobile_sound_off
                null -> R.drawable.ic_m3_mobile
            }
        ),
        contentDescription = stringResource(
            when (action.ring) {
                true -> R.string.filter_action_ring_on_description
                false -> R.string.filter_action_ring_off_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(action.ring),
    )
}

@Composable
private fun BlockActionIcon(action: FilterAction) {
    Icon(
        painter = painterResource(
            when (action.block) {
                BlockType.HANGUP -> R.drawable.ic_m3_phone_cancel
                BlockType.IGNORE -> R.drawable.ic_m3_phone_missed
                BlockType.NONE -> R.drawable.ic_m3_phone_enabled
                null -> R.drawable.ic_m3_phone_enabled
            }
        ),
        contentDescription = stringResource(
            when (action.block) {
                BlockType.HANGUP -> R.string.filter_action_block_hangup_description
                BlockType.IGNORE -> R.string.filter_action_block_ignore_description
                BlockType.NONE -> R.string.filter_action_block_none_description
                null -> R.string.filter_action_system_default_description
            }
        ),
        tint = statusColor(action.block)
    )
}

/**
 * Get the color for a status indicator value.
 */
@Composable
private fun statusColor(status: Any?): Color {
    return when (status) {
        null -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.primary
    }
}

@PreviewLightDark
@Composable
fun PreviewLogEntryListItem() {
    TaclPreview {
        FilterActionIndicator(
            action = FilterAction(),
        )
    }
}