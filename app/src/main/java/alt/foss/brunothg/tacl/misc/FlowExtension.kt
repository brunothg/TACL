/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.misc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Measure the time it takes to emit the first value of a flow.
 */
fun <T> Flow<T>.measureFirstEmissionTime(
    timeSource: TimeSource = TimeSource.Monotonic,
    block: suspend (Duration) -> Unit
): Flow<T> {
    var isFirst = true
    var mark: TimeMark? = null

    return this
        .onStart {
            mark = timeSource.markNow()
        }
        .onEach {
            if (isFirst && mark != null) {
                isFirst = false
                val duration = mark.elapsedNow()

                block(duration)
            }
        }
}