/*
 * Copyright (c) 2026. brunothg
 *
 * This program is licensed under the GNU General Public License version 3
 * or (at your option) any later version. See the LICENSE file for details.
 *
 * See the LICENSE file for the full license text.
 *
 */

package alt.foss.brunothg.tacl.data.local.db.misc.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for [RegexSerializer].
 */
class RegexSerializerTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Serializable
    data class TestRegexContainer(
        @Serializable(with = RegexSerializer::class)
        val regex: Regex
    )

    @Test
    fun `should serialize and deserialize regex with options`() {
        // Given
        val regex = Regex(
            pattern = "//[^\\r\\n]*[\\r\\n]?",
            options = setOf(
                RegexOption.IGNORE_CASE,
                RegexOption.DOT_MATCHES_ALL,
            )
        )
        val container = TestRegexContainer(
            regex = regex
        )

        // When
        val jsonString = json.encodeToString(container)
        val decodedContainer = json.decodeFromString<TestRegexContainer>(jsonString)

        // Then
        assertEquals(regex.pattern, decodedContainer.regex.pattern)
        assertEquals(regex.options, decodedContainer.regex.options)
        assertTrue(decodedContainer.regex.matches("// this is a comment"))
    }
}
