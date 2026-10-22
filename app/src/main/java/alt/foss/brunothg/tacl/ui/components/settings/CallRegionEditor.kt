/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.components.misc.FilterBar
import alt.foss.brunothg.tacl.ui.theme.TaclPreview
import java.util.Locale
import kotlin.math.max

@Immutable
data class CallRegionEditorUiState(
    val callRegion: String? = null,
    val filter: String = "",
) {

    /**
     * The list of available regions.
     */
    val regions: Set<String> = Locale.getISOCountries().toSet()

    /**
     * True if the callRegion is valid.
     */
    val isCallRegionValid: Boolean = callRegion == null || callRegion in regions

    /**
     * True if the entire state is valid.
     */
    val isValid: Boolean = runCatching {
        isCallRegionValid
    }.getOrNull() ?: false

}

@Composable
fun CallRegionEditor(
    state: CallRegionEditorUiState,
    onChange: (CallRegionEditorUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val allCountries = remember(state.regions) {
        state.regions.mapNotNull { region ->
            runCatching {
                Locale.Builder()
                    .setRegion(region)
                    .build()
            }.getOrNull()
        }
            .sortedBy { it.getDisplayCountry(locale) }
    }
    val filtered = remember(state.filter, allCountries) {
        listOf<Locale?>(null) + (
                if (state.filter.isBlank())
                    allCountries
                else
                    allCountries.filter { country ->
                        listOf(
                            country.getDisplayCountry(locale),
                            country.country
                        ).any { countryLbl ->
                            countryLbl.contains(state.filter, ignoreCase = true)
                        }
                    }
                )
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = max(
            0,
            filtered.indexOfFirst { it?.country == state.callRegion })
    )

    Column(
        modifier = modifier,
    ) {
        FilterBar(
            value = state.filter,
            onValueChange = {
                onChange(state.copy(filter = it))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier
                .height(12.dp),
        )

        CallRegionEditorSelectionList(
            state = state,
            onChange = onChange,
            listState = listState,
            locales = filtered,
        )
    }

    // TODO request READ_PHONE_STATE permission for auto
    //    val readPhoneStatePermissionRequest = rememberPermissionRequestLauncher(
    //        permissionDefinition = readPhoneStatePermission,
    //    )
}

@Composable
private fun CallRegionEditorSelectionList(
    state: CallRegionEditorUiState,
    onChange: (CallRegionEditorUiState) -> Unit,
    listState: LazyListState,
    locales: List<Locale?>,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        items(locales) { locale ->
            val label = if (locale == null)
                stringResource(R.string.screen_settings_call_region_auto_detect_label)
            else
                "${locale.getDisplayCountry(LocalLocale.current.platformLocale)} (${locale.country})"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onChange(state.copy(callRegion = locale?.country))
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                )

                if (locale?.country == state.callRegion) {
                    Icon(
                        painter = painterResource(R.drawable.ic_m3_check),
                        contentDescription = stringResource(R.string.misc_state_active),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun buildPreviewCallRegionEditorState(): CallRegionEditorUiState {
    return CallRegionEditorUiState(
        callRegion = null,
    )
}

@PreviewLightDark
@Composable
fun PreviewCallRegionEditor() {
    TaclPreview {
        CallRegionEditor(
            state = buildPreviewCallRegionEditorState(),
            onChange = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        )
    }
}