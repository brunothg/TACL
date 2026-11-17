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
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Unit tests for [JsonRelaxedListSerializer].
 */
class JsonRelaxedCollectionSerializerTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    object TestJsonRelaxedListSerializer :
        JsonRelaxedListSerializer<String>(String.serializer(), unwrapSingleElement = false)

    object TestUnwrapJsonRelaxedListSerializer :
        JsonRelaxedListSerializer<String>(String.serializer(), unwrapSingleElement = true)

    object TestEmptyIsNullListSerializerJson :
        JsonRelaxedListSerializer<String>(String.serializer(), emptyIsNull = true)

    @Serializable
    data class TestListContainer(
        @Serializable(with = TestJsonRelaxedListSerializer::class)
        val list: List<String>?,

        @Serializable(with = TestUnwrapJsonRelaxedListSerializer::class)
        val unwrappedList: List<String>?,

        @Serializable(with = TestJsonRelaxedListSerializer::class)
        val nonNullableList: List<String>,

        @Serializable(with = TestEmptyIsNullListSerializerJson::class)
        val emptyIsNullList: List<String>,
    )

    @Test
    fun `should deserialize single element to list`() {
        // Given
        val jsonString = """
            {
              "list": "hello",
              "unwrappedList": "world",
              "nonNullableList": "item",
              "emptyIsNullList": "placeholder"
            }
        """.trimIndent()

        // When
        val container = json.decodeFromString<TestListContainer>(jsonString)

        // Then
        assertEquals(listOf("hello"), container.list)
        assertEquals(listOf("world"), container.unwrappedList)
        assertEquals(listOf("item"), container.nonNullableList)
        assertEquals(listOf("placeholder"), container.emptyIsNullList)
    }

    @Test
    fun `should deserialize json array to list`() {
        // Given
        val jsonString = """
            {
              "list": ["a","b"],
              "unwrappedList": ["c"],
              "nonNullableList": ["x", "y"],
              "emptyIsNullList": ["1", "2"]
            }
            """.trimIndent()

        // When
        val container = json.decodeFromString<TestListContainer>(jsonString)

        // Then
        assertEquals(listOf("a", "b"), container.list)
        assertEquals(listOf("c"), container.unwrappedList)
        assertEquals(listOf("x", "y"), container.nonNullableList)
        assertEquals(listOf("1", "2"), container.emptyIsNullList)
    }

    @Test
    fun `should serialize list to json array when unwrapSingleElement is false`() {
        // Given
        val container = TestListContainer(
            list = listOf("a"),
            unwrappedList = listOf("c"),
            nonNullableList = listOf("nn"),
            emptyIsNullList = listOf("12"),
        )

        // When
        val jsonString = json.encodeToString(container)

        // Then
        assertEquals(
            """{"list":["a"],"unwrappedList":"c","nonNullableList":["nn"],"emptyIsNullList":["12"]}""",
            jsonString
        )
    }

    @Test
    fun `should serialize multiple elements list to json array when unwrapSingleElement is true`() {
        // Given
        val container = TestListContainer(
            list = listOf("a", "b"),
            unwrappedList = listOf("c", "d"),
            nonNullableList = listOf("nn1", "nn2"),
            emptyIsNullList = listOf("1", "2"),
        )

        // When
        val jsonString = json.encodeToString(container)

        // Then
        assertEquals(
            """{"list":["a","b"],"unwrappedList":["c","d"],"nonNullableList":["nn1","nn2"],"emptyIsNullList":["1","2"]}""",
            jsonString
        )
    }

    @Test
    fun `should convert null to empty list for nonNullableList while preserving null for nullable list`() {
        // Given
        val jsonString = """
            {
              "list": null,
              "unwrappedList": null,
              "nonNullableList": null,
              "emptyIsNullList": null
            }
            """.trimIndent()

        // When
        val container = json.decodeFromString<TestListContainer>(jsonString)

        // Then
        assertNull(container.list)
        assertNull(container.unwrappedList)
        assertEquals(emptyList<String>(), container.nonNullableList)
    }

    @Test
    fun `should serialize null property to json null`() {
        // Given
        val container = TestListContainer(
            list = null,
            unwrappedList = null,
            nonNullableList = listOf("nn"),
            emptyIsNullList = listOf("1")
        )

        // When
        val jsonString = json.encodeToString(container)

        // Then
        assertEquals(
            """{"list":null,"unwrappedList":null,"nonNullableList":["nn"],"emptyIsNullList":["1"]}""",
            jsonString
        )
    }

    @Test
    fun `should serialize empty list to json null when emptyIsNull is true`() {
        // Given
        val container = TestListContainer(
            list = null,
            unwrappedList = null,
            nonNullableList = listOf("nn"),
            emptyIsNullList = emptyList()
        )

        // When
        val jsonString = json.encodeToString(container)

        // Then
        assertEquals(
            """{"list":null,"unwrappedList":null,"nonNullableList":["nn"],"emptyIsNullList":null}""",
            jsonString
        )
    }

}
