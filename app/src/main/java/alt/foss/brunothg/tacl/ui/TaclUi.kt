/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import alt.foss.brunothg.tacl.ui.screens.main.MainScaffold
import alt.foss.brunothg.tacl.ui.screens.setup.SetupScaffold
import alt.foss.brunothg.tacl.ui.theme.TaclTheme

/**
 * The main UI of the application.
 */
@Composable
fun TaclUi() {
    TaclTheme {
        var isSetup by remember { mutableStateOf(true) }

        Surface(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            AnimatedContent(isSetup) { animatedIsSetup ->
                if (animatedIsSetup) {
                    SetupScaffold(
                        onDone = {
                            Log.d("TaclUi", "Setup done")
                            isSetup = false
                        },
                    )
                } else {
                    MainScaffold()
                }
            }
        }
    }
}
