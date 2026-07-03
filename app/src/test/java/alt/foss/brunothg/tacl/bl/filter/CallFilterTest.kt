/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.bl.filter

import alt.foss.brunothg.tacl.bl.Call
import alt.foss.brunothg.tacl.bl.filter.CallFilter.Companion.filterCall
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.net.URI

/**
 * Unit tests for [CallFilter].
 */
class CallFilterTest {

    @Test
    fun `filterCall should return merged action of all matching enabled filters`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Filter 1",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(block = BlockType.IGNORE),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter 2",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(ring = false),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.IGNORE, result?.block)
        assertEquals(false, result?.ring)
    }

    @Test
    fun `filterCall should respect invertMatch property`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Inverted Matcher",
                matcher = RegexCallMatcher(Regex(".*999.*")), // Matcher returns false
                invertMatch = true,            // But we invert it
                action = FilterAction(block = BlockType.HANGUP),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.HANGUP, result?.block)
    }

    @Test
    fun `filterCall should ignore disabled filters`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Disabled Filter",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(block = BlockType.IGNORE),
                enabled = false
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertNull(result)
    }

    @Test
    fun `filterCall should return null if no filter matches`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Non-matching Filter",
                matcher = RegexCallMatcher(Regex(".*999.*")),
                action = FilterAction(block = BlockType.IGNORE),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertNull(result)
    }

    @Test
    fun `filterCall should merge actions correctly prioritizing earlier filters for specific fields`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Filter A",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(block = BlockType.IGNORE, ring = true),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter B",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(block = BlockType.HANGUP, notification = false),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.IGNORE, result?.block)
        assertEquals(true, result?.ring)
        assertEquals(false, result?.notification)
    }
}
