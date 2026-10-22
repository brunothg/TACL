/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.net.URI

/**
 * Unit tests for [Call].
 */
class CallTest {

    @Test
    fun `phoneNumber should return normalized number when possible`() {
        // Given
        val call = Call(
            identifier = URI("tel:0123456789"),
            region = "DE"
        )

        // When
        val phoneNumber = call.phoneNumber

        // Then
        assertEquals("+49123456789", phoneNumber, "Number should be normalized")
        assertEquals("+49123456789", call.normalizedPhoneNumber, "Normalized number should be available")
    }

    @Test
    fun `phoneNumber should return raw number when normalization fails`() {
        // Given
        val call = Call(
            identifier = URI("tel:invalid-number"),
            region = "DE"
        )

        // When
        val phoneNumber = call.phoneNumber

        // Then
        assertEquals("invalid-number", phoneNumber, "Number should be raw")
        assertNull(call.normalizedPhoneNumber, "Normalized number should not be available")
    }

    @Test
    fun `rawPhoneNumber should return scheme specific part`() {
        // Given
        val uri = URI("tel:+49123456789")
        val call = Call(identifier = uri)

        // When
        val raw = call.rawPhoneNumber

        // Then
        assertEquals("+49123456789", raw, "Raw number should be extracted")
    }

    @Test
    fun `phoneNumber should handle international format without region`() {
        // Given
        val call = Call(
            identifier = URI("tel:+49123456789"),
            region = null
        )

        // When
        val phoneNumber = call.phoneNumber

        // Then
        assertEquals("+49123456789", phoneNumber, "Number should be normalized")
    }
}
