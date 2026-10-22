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

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * Utilities for using with compose previews.
 */
object PreviewUtils {

    /**
     * Returns the value of [runtimeValue] if not in preview mode, otherwise returns the value of [previewValue].
     *
     * @param runtimeValue The value to return if not in preview mode.
     * @param previewValue The value to return if in preview mode.
     * @return Depending on the value of [LocalInspectionMode], either [runtimeValue] or [previewValue].
     */
    @Composable
    fun <T> withPreviewFallback(
        runtimeValue: @Composable () -> T,
        previewValue: @Composable () -> T,
    ): T =
        if (LocalInspectionMode.current) {
            previewValue()
        } else {
            runtimeValue()
        }


}