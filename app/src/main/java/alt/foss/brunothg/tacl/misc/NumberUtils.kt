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

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

/**
 * Utility functions for numbers.
 */
object NumberUtils {

    /**
     * Ordered array of SI suffixes for large numbers.
     */
    val SI_SUFFIXES = arrayOf("", "K", "M", "G", "T", "P", "E")

    /**
     * Convert a number to a human-readable string with SI suffix for large numbers.
     */
    fun Number.toHumanReadable(): String {
        val value = this.toDouble()
        val absoluteValue = abs(value)

        if (value == 0.0) {
            return "0"
        }

        val exponentGroup = (log10(absoluteValue).toInt() / 3)
            .coerceAtMost(SI_SUFFIXES.lastIndex)
        val suffix = SI_SUFFIXES[exponentGroup]
        val scaledValue = value / 10.0.pow(exponentGroup * 3)
        val decimalPlaces = if (exponentGroup == 0) 0 else 1

        return "%.${decimalPlaces}f%s".format(scaledValue, suffix)
    }

}