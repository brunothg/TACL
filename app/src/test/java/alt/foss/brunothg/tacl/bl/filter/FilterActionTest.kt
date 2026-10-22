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
        assertEquals(BlockType.IGNORE, result.block, "Should favor first action's block type")
        assertEquals(true, result.ring, "Should favor first action's ring setting")
        assertEquals(true, result.notification, "Should take second action's notification since first action's is null")
        assertEquals(true, result.callLog, "Should favor first action's call log setting")
    }

    @Test
    fun `plus operator should result in null if both properties are null`() {
        // Given
        val action1 = FilterAction(block = null)
        val action2 = FilterAction(block = null)

        // When
        val result = action1 + action2

        // Then
        assertNull(result.block, "Should result in null if both properties are null")
    }

    @Test
    fun `plus operator with NOTHING actions should return NOTHING action`() {
        // Given
        val action1 = FilterAction.NOTHING
        val action2 = FilterAction.NOTHING

        // When
        val result = action1 + action2

        // Then
        assertNull(result.block, "Should result in empty action")
        assertNull(result.ring, "Should result in empty action")
        assertNull(result.notification, "Should result in empty action")
        assertNull(result.callLog, "Should result in empty action")
    }
}
