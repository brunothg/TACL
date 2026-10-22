/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterDao
import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterEntity
import alt.foss.brunothg.tacl.data.local.db.log.FilterLogDao
import alt.foss.brunothg.tacl.data.local.db.log.FilterLogEntryEntity
import alt.foss.brunothg.tacl.data.local.db.misc.converters.BlockTypeConverter
import alt.foss.brunothg.tacl.data.local.db.misc.converters.CallMatcherDtoConverter
import alt.foss.brunothg.tacl.data.local.db.misc.converters.IdConverter
import alt.foss.brunothg.tacl.data.local.db.misc.converters.InstantConverter
import alt.foss.brunothg.tacl.data.local.db.misc.converters.UriConverter

/**
 * The application database.
 */
@Database(
    entities = [
        CallFilterEntity::class,
        FilterLogEntryEntity::class,
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    IdConverter::class,
    InstantConverter::class,
    UriConverter::class,
    BlockTypeConverter::class,
    CallMatcherDtoConverter::class,
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Get the DAO for call filters.
     */
    abstract fun callFilterDao(): CallFilterDao

    /**
     * Get the DAO for the filter log.
     */
    abstract fun filterLogDao(): FilterLogDao

}