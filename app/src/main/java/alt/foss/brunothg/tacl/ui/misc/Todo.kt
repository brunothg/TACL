/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.misc

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

/**
 * A placeholder UI for features not yet implemented.
 *
 * @param message The message to display.
 */
@Composable
fun UiTodo(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Log.w("TODO", "TODO: $message")
        Text(
            text = "TODO: $message",
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A placeholder action for features not yet implemented.
 *
 * @param message The message to display.
 */
@Composable
fun ActionTodo(message: String) {
    val context = LocalContext.current

    Toast.makeText(context, "TODO: $message", Toast.LENGTH_SHORT).show()
}