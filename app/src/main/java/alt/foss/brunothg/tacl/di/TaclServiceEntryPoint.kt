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

import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import alt.foss.brunothg.tacl.bl.repository.CallFilterRepository
import alt.foss.brunothg.tacl.service.TaclService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for [TaclService] to access repositories.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface TaclServiceEntryPoint {

    /**
     * Get the call filter repository.
     */
    fun callFilterRepository(): CallFilterRepository

    /**
     * Get the app settings repository.
     */
    fun appSettingsRepository(): AppSettingsRepository

}