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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * Abstract base class for relaxed collection types supporting array, single and null value types.
 *
 * ```kotlin
 * @Serializable(with = JsonRelaxedCollectionSerializer::class)
 * val stringList: List<String>
 *
 * @Serializable(with = JsonRelaxedCollectionSerializer::class)
 * val stringSet: Set<String>? = null
 * ```
 *
 * @param collectionSerializer The underlying collection serializer
 * @param unwrapSingleElement If true single elements won't be serialized as an array, but as an object/primitive instead
 * @param emptyIsNull If true an empty collection will be serialized as null
 */
abstract class JsonRelaxedCollectionSerializer<C : Collection<*>>(
    collectionSerializer: KSerializer<C>,
    private val unwrapSingleElement: Boolean = false,
    private val emptyIsNull: Boolean = false,
) : JsonTransformingSerializer<C>(collectionSerializer) {

    override fun transformDeserialize(element: JsonElement): JsonElement =
        when (element) {
            is JsonArray -> element
            is JsonNull -> JsonArray(emptyList())
            else -> JsonArray(listOf(element))
        }

    override fun transformSerialize(element: JsonElement): JsonElement =
        if (unwrapSingleElement && element is JsonArray && element.size == 1) {
            element.first()
        } else if (emptyIsNull && element is JsonArray && element.isEmpty()) {
            JsonNull
        } else {
            element
        }

}

/**
 * [JsonRelaxedCollectionSerializer] featuring [List]s
 *
 * @see JsonRelaxedCollectionSerializer
 */
open class JsonRelaxedListSerializer<T>(
    elementSerializer: KSerializer<T>,
    unwrapSingleElement: Boolean = false,
    emptyIsNull: Boolean = false,
) : JsonRelaxedCollectionSerializer<List<T>>(
    collectionSerializer = ListSerializer(elementSerializer),
    unwrapSingleElement = unwrapSingleElement,
    emptyIsNull = emptyIsNull
)
