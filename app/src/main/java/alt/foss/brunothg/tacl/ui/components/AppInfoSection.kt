/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import alt.foss.brunothg.tacl.BuildConfig
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * A section displaying application information.
 */
@Composable
fun AppInfoSection(
    modifier: Modifier = Modifier,
) {
    val appName = stringResource(R.string.app_name)
    val versionName = BuildConfig.VERSION_NAME
    val versionCode = BuildConfig.VERSION_CODE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            stringResource(R.string.screen_settings_app_info_label),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        InfoRow(stringResource(R.string.screen_settings_app_info_name), appName)
        InfoRow(
            stringResource(R.string.screen_settings_app_info_version),
            "$versionName ($versionCode)"
        )
        InfoLinkRow(
            stringResource(R.string.screen_settings_app_info_vcs),
            stringResource(R.string.screen_settings_app_info_vcs_url)
        )
    }
}

@Composable
private fun Row(
    label: String,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        content()
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(
        label = label
    ) {
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InfoLinkRow(
    label: String,
    url: String,
) {
    Row(
        label = label
    ) {
        Link(
            url = url,
            text = url.toUri().host ?: url,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewAppInfoSection() {
    TaclPreview {
        AppInfoSection()
    }
}
