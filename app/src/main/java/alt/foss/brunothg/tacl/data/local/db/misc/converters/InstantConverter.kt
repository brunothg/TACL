/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.misc.converters

import androidx.room.TypeConverter
import kotlin.time.Instant

/**
 * Type converter for [Instant].
 */
object InstantConverter {
    /**
     * Convert [Instant] to database value.
     */
    @TypeConverter
    fun toDb(instant: Instant?): Long? = instant?.toEpochMilliseconds()

    /**
     * Convert database value to [Instant].
     */
    @TypeConverter
    fun fromDb(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }
}
