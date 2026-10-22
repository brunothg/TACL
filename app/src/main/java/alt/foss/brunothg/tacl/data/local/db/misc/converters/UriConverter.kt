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
import java.net.URI

/**
 * Type converter for [URI].
 */
object UriConverter {
    /**
     * Convert [URI] to database value.
     */
    @TypeConverter
    fun toDb(instant: URI?): String? = instant?.toString()

    /**
     * Convert database value to [URI].
     */
    @TypeConverter
    fun fromDb(value: String?): URI? = value?.let { URI(it) }
}
