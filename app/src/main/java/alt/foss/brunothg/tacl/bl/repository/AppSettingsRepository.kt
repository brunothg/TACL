/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for application settings.
 */
interface AppSettingsRepository {

    /**
     * The current setup version.
     */
    val setupVersion: Flow<Int>

    /**
     * Set the setup version.
     *
     * @param version The new version.
     */
    suspend fun setSetupVersion(
        version: Int
    )

    /**
     * The current call region.
     * Manual value or null for auto.
     */
    val callRegion: Flow<String?>

    /**
     * Set the call region.
     *
     * @param region The new region or null for auto.
     */
    suspend fun setCallRegion(
        region: String?
    )
}