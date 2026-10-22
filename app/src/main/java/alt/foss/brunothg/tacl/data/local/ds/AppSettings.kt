/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.ds

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Keys for application settings in DataStore.
 */
object AppSettings {
    /**
     * The key for the setup version.
     */
    val SetupVersion = intPreferencesKey("setup_version")

    /**
     * The key for the call region.
     */
    val CallRegion = stringPreferencesKey("call_region")

    /**
     * The key for the filter max log size (max log entries).
     */
    val LogMaxSize = intPreferencesKey("log_max_size")
}