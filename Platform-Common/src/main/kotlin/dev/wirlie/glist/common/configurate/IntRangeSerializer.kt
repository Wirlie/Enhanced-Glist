package dev.wirlie.glist.common.configurate

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type

class IntRangeSerializer: TypeSerializer<IntRange> {

    override fun deserialize(type: Type, node: ConfigurationNode): IntRange {
        val value = node.string!!
        val parts = value.split("..")

        if(parts.size != 2) {
            throw SerializationException("Cannot deserialize IntRange from string '$value'")
        }

        val start = parts[0].toIntOrNull() ?: throw SerializationException("Cannot deserialize IntRange from string '$value'")
        val end = parts[1].toIntOrNull() ?: throw SerializationException("Cannot deserialize IntRange from string '$value'")

        if(end < start) {
            throw SerializationException("Cannot deserialize IntRange from string '$value': start is greater than end")
        }

        return IntRange(start, end)
    }

    override fun serialize(type: Type, obj: IntRange?, node: ConfigurationNode) {
        if(obj == null) {
            node.set(null)
        } else {
            node.set("${obj.first}..${obj.last}")
        }
    }

}
