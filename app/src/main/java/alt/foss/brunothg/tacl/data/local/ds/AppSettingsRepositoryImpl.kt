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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import alt.foss.brunothg.tacl.bl.repository.AppSettingsRepository
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

/**
 * Implementation of [AppSettingsRepository] using DataStore.
 *
 * @property dataStore The preferences datastore.
 */
class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppSettingsRepository {

    override val setupVersion = dataStore.data.map {
        it[AppSettings.SetupVersion] ?: 0
    }

    override suspend fun setSetupVersion(version: Int) {
        dataStore.edit {
            it[AppSettings.SetupVersion] = version
        }
    }

    override val callRegion = dataStore.data.map {
        it[AppSettings.CallRegion].let { value ->
            if (value.isNullOrEmpty()) null else value
        }
    }

    override suspend fun setCallRegion(region: String?) {
        dataStore.edit {
            val key = AppSettings.CallRegion
            if (region == null) {
                it.remove(key)
            } else {
                require(region.isNotBlank())
                require(region in Locale.getISOCountries())

                it[key] = region
            }
        }
    }

}