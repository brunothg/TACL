/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.ui.components.misc

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewLightDark
import alt.foss.brunothg.tacl.ui.theme.TaclPreview


/**
 * A clickable link.
 *
 * @param url The URL to open.
 * @param modifier The modifier to be applied to the text.
 * @param text The text to display.
 * @param color The color of the link.
 * @param style The text style.
 * @param linkInteractionListener Optional listener for link interactions.
 */
@Composable
fun Link(
    url: String,
    modifier: Modifier = Modifier,
    text: String = url,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = LocalTextStyle.current,
    linkInteractionListener: LinkInteractionListener? = null,
) {
    Text(
        text = buildLink(
            url = url,
            text = text,
            color = color,
            linkInteractionListener = linkInteractionListener,
        ),
        modifier = modifier,
        style = style,
    )
}

/**
 * Build an annotated string for a link.
 *
 * @param url The URL.
 * @param text The text.
 * @param color The color.
 * @param linkInteractionListener Optional listener for link interactions.
 * @return The annotated string.
 */
fun buildLink(
    url: String,
    text: String = url,
    color: Color = Color.Cyan,
    linkInteractionListener: LinkInteractionListener? = null,
): AnnotatedString {
    return buildAnnotatedString {
        withLink(
            LinkAnnotation.Url(
                url = url,
                linkInteractionListener = linkInteractionListener,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = color,
                        textDecoration = TextDecoration.Underline
                    ),
                    focusedStyle = SpanStyle(
                        color = color.copy(alpha = 0.7f),
                    ),
                    pressedStyle = SpanStyle(
                        color = color.copy(alpha = 0.5f),
                    ),
                ),
            )
        ) {
            append(text)
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewLink() {
    TaclPreview {
        Link(
            url = "https://localhost/"
        )
    }
}
