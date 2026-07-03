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
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
private data class RegexSurrogate(
    val pattern: String,
    val options: List<String> = emptyList()
)

/**
 * Serializer for [Regex].
 */
object RegexSerializer : KSerializer<Regex> {
    private val surrogateSerializer = RegexSurrogate.serializer()
    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Regex) {
        encoder.encodeSerializableValue(
            surrogateSerializer,
            RegexSurrogate(
                value.pattern,
                value.options.map { it.name }
            )
        )
    }

    override fun deserialize(decoder: Decoder): Regex {
        val surrogateValue = decoder.decodeSerializableValue(surrogateSerializer)
        return Regex(
            surrogateValue.pattern,
            surrogateValue.options.map { RegexOption.valueOf(it) }.toSet()
        )
    }
}