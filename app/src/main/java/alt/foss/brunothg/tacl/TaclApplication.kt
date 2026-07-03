/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl

import android.app.Application
import android.content.Context
import alt.foss.brunothg.tacl.service.TaclService
import dagger.hilt.android.HiltAndroidApp

/**
 * The application class.
 */
@HiltAndroidApp
class TaclApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        val callScreeningService = TaclService::class

        lateinit var instance: TaclApplication
            private set

        val context: Context
            get() = instance.applicationContext
    }

}