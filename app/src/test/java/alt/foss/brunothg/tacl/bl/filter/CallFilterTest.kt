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
import alt.foss.brunothg.tacl.bl.filter.CallFilter.Companion.filterCallWithIntermediate
import alt.foss.brunothg.tacl.bl.filter.matchers.RegexCallMatcher
import org.junit.jupiter.api.Assertions.assertEquals
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
                action = FilterAction(
                    block = BlockType.IGNORE
                ),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter 2",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(
                    ring = false
                ),
                enabled = true
            )
        )
            .sortedBy { it.orderIndex }

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.IGNORE, result.block, "Merged block type should be IGNORE")
        assertEquals(false, result.ring, "Merged ring should be false")
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
                action = FilterAction(
                    block = BlockType.HANGUP
                ),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.HANGUP, result.block, "Merged block type should be HANGUP")
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
                action = FilterAction(
                    block = BlockType.IGNORE
                ),
                enabled = false
            )
        )

        // When
        val result = filters.filterCall(
            call = call,
        )

        // Then
        assertEquals(FilterAction.NOTHING, result, "Result action should be NOTHING action")
    }

    @Test
    fun `filterCall should return NOTHING action if no filter matches`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Non-matching Filter",
                matcher = RegexCallMatcher(Regex(".*999.*")),
                action = FilterAction(
                    block = BlockType.IGNORE
                ),
                enabled = true
            )
        )

        // When
        val result = filters.filterCall(
            call = call,
        )

        // Then
        assertEquals(FilterAction.NOTHING, result, "Result action should be NOTHING action")
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
                action = FilterAction(
                    block = BlockType.IGNORE,
                    ring = true
                ),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter B",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(
                    block = BlockType.HANGUP,
                    notification = false
                ),
                enabled = true
            )
        )
            .sortedBy { it.orderIndex }

        // When
        val result = filters.filterCall(call)

        // Then
        assertEquals(BlockType.IGNORE, result.block, "Merged block type should be IGNORE")
        assertEquals(true, result.ring, "Merged ring should be true")
        assertEquals(false, result.notification, "Merged notification should be false")
    }

    @Test
    fun `filterCallWithIntermediate should return intermediate actions`() {
        // Given
        val call = Call(identifier = URI("tel:123"))
        val filters = listOf(
            CallFilter(
                orderIndex = 0,
                name = "Filter A",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(
                    block = BlockType.IGNORE,
                    ring = true
                ),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter Disabled",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(
                    block = BlockType.NONE,
                    notification = false,
                    callLog = false,
                    ring = true,
                ),
                enabled = false
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter Disabled",
                matcher = RegexCallMatcher(Regex("^$")),
                action = FilterAction(
                    block = BlockType.NONE,
                    notification = false,
                    callLog = false,
                    ring = true,
                ),
                enabled = true
            ),
            CallFilter(
                orderIndex = 1,
                name = "Filter B",
                matcher = RegexCallMatcher(Regex(".*123.*")),
                action = FilterAction(
                    block = BlockType.HANGUP,
                    notification = false
                ),
                enabled = true
            )
        )
            .sortedBy { it.orderIndex }

        // When
        val intermediate = filters.filterCallWithIntermediate(call)
        val result = filters.filterCall(call)

        // Then
        assertEquals(result, intermediate.second, "Intermediate conclusion should match result")
        assertEquals(
            listOf(
                Pair(filters[0], FilterAction(block = BlockType.IGNORE, ring = true)),
                Pair(filters[3], FilterAction(block = BlockType.IGNORE, ring = true, notification = false)),
            ),
            intermediate.first,
            "Intermediate actions should be correct"
        )
    }
}
