/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.permission

import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * State of a permission.
 *
 * @property definition The permission definition.
 * @property granted True if the permission is granted.
 */
data class PermissionState(
    val definition: PermissionDefinition,
    val granted: Boolean,
)

/**
 * Manager for permissions and roles.
 *
 * @property context The application context.
 * @property definitions The set of permission definitions to manage.
 */
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val definitions: Set<PermissionDefinition>,
) {

    private val roleManager = context.getSystemService(RoleManager::class.java)

    private val _state = MutableStateFlow<Set<PermissionState>>(emptySet())

    /**
     * The current state of all managed permissions.
     */
    val state = _state.asStateFlow()

    private val appLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            refreshState()
        }
    }

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        refreshState()
    }

    /**
     * Check if a permission is granted.
     *
     * @param permissionDefinition The permission definition.
     * @return True if granted.
     */
    fun isGranted(permissionDefinition: PermissionDefinition): Boolean {
        return when (permissionDefinition.type) {
            PermissionType.Role -> roleManager.isRoleHeld(permissionDefinition.name)

            PermissionType.RuntimePermission -> ContextCompat.checkSelfPermission(
                context,
                permissionDefinition.name
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Refresh the permission state.
     */
    fun refreshState() {
        _state.value = definitions
            .map { pd ->
                PermissionState(
                    definition = pd,
                    granted = isGranted(pd),
                )
            }
            .toSet()
    }

}