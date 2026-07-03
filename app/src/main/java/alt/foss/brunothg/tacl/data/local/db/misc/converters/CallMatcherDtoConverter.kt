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
import alt.foss.brunothg.tacl.data.local.db.filter.matchers.CallMatcherDto
import kotlinx.serialization.json.Json

/**
 * Type converter for [CallMatcherDto].
 */
object CallMatcherDtoConverter {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Convert [CallMatcherDto] to database value.
     */
    @TypeConverter
    fun toDb(dto: CallMatcherDto): String = json.encodeToString(CallMatcherDto.serializer(), dto)

    /**
     * Convert database value to [CallMatcherDto].
     */
    @TypeConverter
    fun fromDb(value: String): CallMatcherDto =
        json.decodeFromString(CallMatcherDto.serializer(), value)

}