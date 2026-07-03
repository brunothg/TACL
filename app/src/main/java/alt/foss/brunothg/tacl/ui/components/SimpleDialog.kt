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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

typealias SimpleDialogButtons = List<@Composable () -> Unit>

@Composable
fun SimpleDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    header: (@Composable () -> Unit)? = null,
    buttons: SimpleDialogButtons = emptyList(),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
            ) {

                if (header != null) {
                    header()
                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f, fill = false),
                ) {
                    content()
                }

                if (buttons.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        for (button in buttons) {
                            button()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun buildSimpleDialogDefaultButtons(
    cancelLabel: String? = null,
    onCancel: (() -> Unit)? = null,
    okLabel: String? = null,
    onOk: (() -> Unit)? = null,
): SimpleDialogButtons {
    return listOf(
        onCancel to (cancelLabel ?: stringResource(R.string.misc_action_cancel)),
        onOk to (okLabel ?: stringResource(R.string.misc_action_ok)),
    )
        .filter { it.first != null }
        .map { (it.first as () -> Unit) to it.second }
        .map { ele ->
            val action = ele.first
            val label = ele.second

            @Composable {
                TextButton(
                    onClick = action
                ) {
                    Text(
                        text = label
                    )
                }
            }
        }
}

@PreviewLightDark
@Composable
fun PreviewSimpleDialog() {
    TaclPreview {
        SimpleDialog(
            header = {
                Text(
                    text = "PreviewSimpleDialog",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            onDismiss = {},
            buttons = buildSimpleDialogDefaultButtons(
                onCancel = {},
                onOk = {},
            )
        ) {
            Text(
                text = "PreviewSimpleDialog",
                modifier = Modifier
                    .padding(16.dp),
            )
        }
    }
}