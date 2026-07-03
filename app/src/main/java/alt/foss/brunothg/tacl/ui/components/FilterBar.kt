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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.R
import alt.foss.brunothg.tacl.ui.theme.TaclPreview

/**
 * A search bar for filtering lists.
 *
 * @param modifier The modifier to be applied to the text field.
 * @param shape The shape of the text field.
 * @param value The current value of the text field.
 * @param onValueChange Callback when the value changes.
 * @param placeholder The placeholder text.
 */
@Composable
fun FilterBar(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
){
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = modifier,
        shape = shape,
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
        }),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_m3_search),
                contentDescription = stringResource(R.string.misc_action_filter),
            )
        },
        placeholder = {
            Text(
                placeholder ?: stringResource(R.string.misc_action_search)
            )
        }
    )
}

@PreviewLightDark
@Composable
fun PreviewFilterBar() {
    TaclPreview {
        FilterBar(
            value="",
            onValueChange = {},
        )
    }
}