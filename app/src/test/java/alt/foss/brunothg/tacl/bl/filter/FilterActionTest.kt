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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Unit tests for [FilterAction].
 */
class FilterActionTest {

    @Test
    fun `plus operator should merge properties correctly favoring the first action`() {
        // Given
        val action1 = FilterAction(
            block = BlockType.IGNORE,
            ring = true,
            notification = null,
            callLog = true
        )
        val action2 = FilterAction(
            block = BlockType.HANGUP,
            ring = false,
            notification = true,
            callLog = false
        )

        // When
        val result = action1 + action2

        // Then
        assertEquals(BlockType.IGNORE, result.block, "Should favor action1's block type")
        assertEquals(true, result.ring, "Should favor action1's ring setting")
        assertEquals(true, result.notification, "Should take action2's notification since action1's is null")
        assertEquals(true, result.callLog, "Should favor action1's call log setting")
    }

    @Test
    fun `plus operator should result in null if both properties are null`() {
        // Given
        val action1 = FilterAction(block = null)
        val action2 = FilterAction(block = null)

        // When
        val result = action1 + action2

        // Then
        assertNull(result.block)
    }

    @Test
    fun `plus operator with empty actions should return empty action`() {
        // Given
        val action1 = FilterAction()
        val action2 = FilterAction()

        // When
        val result = action1 + action2

        // Then
        assertNull(result.block)
        assertNull(result.ring)
        assertNull(result.notification)
        assertNull(result.callLog)
    }
}
