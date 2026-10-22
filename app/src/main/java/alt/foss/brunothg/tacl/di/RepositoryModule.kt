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

import alt.foss.brunothg.tacl.data.local.db.filter.CallFilterRepositoryImpl
import alt.foss.brunothg.tacl.data.local.ds.AppSettingsRepositoryImpl
import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import alt.foss.brunothg.tacl.bl.repository.CallFilterRepository
import alt.foss.brunothg.tacl.bl.repository.FilterLogRepository
import alt.foss.brunothg.tacl.data.local.db.log.FilterLogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Bind the app settings repository.
     */
    @Binds
    abstract fun bindAppSettingsRepository(
        impl: AppSettingsRepositoryImpl
    ): AppSettingsRepository

    /**
     * Bind the call filter repository.
     */
    @Binds
    abstract fun bindCallFilterRepository(
        impl: CallFilterRepositoryImpl
    ): CallFilterRepository

    /**
     * Bind the filter log repository.
     */
    @Binds
    abstract fun bindFilterLogRepository(
        impl: FilterLogRepositoryImpl
    ): FilterLogRepository

}