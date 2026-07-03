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


/**
 * State of a loadable value by an async loading operation.
 */
sealed interface LoadState<out T> {

    /**
     * Get the value if loaded or null instead.
     */
    fun <T> LoadState<T>.valueOrNull(): T? =
        when (this) {
            is Loaded -> value
            Loading -> null
        }

    /**
     * Get the value if loaded or throw an exception instead.
     */
    fun <T> LoadState<T>.valueOrThrow(): T =
        when (this) {
            is Loaded -> value
            Loading -> error("Value is not loaded yet")
        }

    /**
     * Not loaded yet.
     * No value available
     */
    object Loading : LoadState<Nothing>

    /**
     * Value is loaded.
     */
    data class Loaded<T>(val value: T) : LoadState<T>
}