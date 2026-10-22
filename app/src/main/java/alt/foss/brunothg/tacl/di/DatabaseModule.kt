/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.di

import android.content.Context
import androidx.room.Room
import alt.foss.brunothg.tacl.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for the database.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provide the app database.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app.db"
    )
        .fallbackToDestructiveMigration(true)
        .build()

    /**
     * Provide the call filter DAO.
     */
    @Provides
    fun provideCallFilterDao(db: AppDatabase) = db.callFilterDao()

    /**
     * Provide the filter log DAO.
     */
    @Provides
    fun provideFilterLogDao(db: AppDatabase) = db.filterLogDao()

}