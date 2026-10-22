/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.filter.matchers

import alt.foss.brunothg.tacl.bl.Call
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URI

/**
 * Unit tests for [RegexCallMatcher].
 */
class RegexCallMatcherTest {

    @Test
    fun `appliesTo should return true if regex matches phone number`() {
        // Given
        val matcher = RegexCallMatcher(Regex(".*123.*"))
        val call = Call(identifier = URI("tel:+49123456789"))

        // When
        val result = matcher.appliesTo(call)

        // Then
        assertTrue(result, "Regex should match")
    }

    @Test
    fun `appliesTo should return false if regex does not match phone number`() {
        // Given
        val matcher = RegexCallMatcher(Regex("^\\+49.*"))
        val call = Call(identifier = URI("tel:+1123456789"))

        // When
        val result = matcher.appliesTo(call)

        // Then
        assertFalse(result, "Regex should not match")
    }

    @Test
    fun `appliesTo should match against normalized phone number`() {
        // Given
        // "0123" in DE region normalizes to "+49123"
        val matcher = RegexCallMatcher(Regex("^\\+49.*"))
        val call = Call(
            identifier = URI("tel:0123456789"),
            region = "DE"
        )

        // When
        val result = matcher.appliesTo(call)

        // Then
        assertTrue(result, "Regex should match")
    }
}
