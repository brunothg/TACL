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
import alt.foss.brunothg.tacl.bl.filter.BlockType

/**
 * Type converter for [BlockType].
 */
object BlockTypeConverter {
    /**
     * Convert [BlockType] to database value.
     */
    @TypeConverter
    fun toDb(enum: BlockType): String = enum.id

    /**
     * Convert database value to [BlockType].
     */
    @TypeConverter
    fun fromDb(value: String): BlockType = BlockType.entries.firstOrNull { it.id == value }
        ?: error("Database mismatch: Unknown id '$value' for ${BlockType::class.simpleName}")
}