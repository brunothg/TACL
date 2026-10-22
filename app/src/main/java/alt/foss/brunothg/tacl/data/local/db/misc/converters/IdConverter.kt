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
import alt.foss.brunothg.tacl.misc.ID

/**
 * Type converter for [ID].
 */
object IdConverter {
    /**
     * Convert [ID] to database value.
     */
    @TypeConverter
    fun toDb(id: ID?): String? = id?.value

    /**
     * Convert database value to [ID].
     */
    @TypeConverter
    fun fromDb(value: String?): ID? = value?.let { ID(it) }
}
