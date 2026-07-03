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

import alt.foss.brunothg.tacl.bl.permission.AppPermissions
import alt.foss.brunothg.tacl.bl.permission.PermissionDefinition
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for permissions.
 */
@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {

    /**
     * Provide the set of permission definitions.
     */
    @Provides
    @Singleton
    fun provideAppPermissionDefinitions(): Set<PermissionDefinition> = AppPermissions.entries
        .map { p -> p.definition }
        .toSet()

}