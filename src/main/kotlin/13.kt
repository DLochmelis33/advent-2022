import kotlinx.serialization.json.*
import java.io.File

sealed class PacketStruct {
    abstract operator fun compareTo(other: PacketStruct): Int
}

class ListPacket(val items: List<PacketStruct>) : PacketStruct() {
    override fun compareTo(other: PacketStruct): Int {
        return when (other) {
            is IntPacket -> compareTo(ListPacket(listOf(other)))
            is ListPacket -> (items zip other.items)
                .asSequence()
                .map { (a, b) -> a.compareTo(b) }
                .firstOrNull { it != 0 } ?: (items.size - other.items.size)
        }
    }
}

class IntPacket(val value: Int) : PacketStruct() {
    override fun compareTo(other: PacketStruct): Int {
        return when (other) {
            is IntPacket -> value.compareTo(other.value)
            is ListPacket -> ListPacket(listOf(this)).compareTo(other)
        }
    }
}

fun main() {
    val input = File("in.txt").readLines().filter { it.isNotEmpty() }.chunked(2).map { (a, b) -> a to b }
    var result = 0
    fun parseToPacket(s: String): PacketStruct {
        fun parseElem(e: JsonElement): PacketStruct = when (e) {
            is JsonPrimitive -> IntPacket(e.int)
            is JsonArray -> ListPacket(e.map { parseElem(it) })
            else -> error("cannot parse $e")
        }
        return parseElem(Json.parseToJsonElement(s))
    }
    for ((i, t) in input.withIndex()) {
        val a = parseToPacket(t.first)
        val b = parseToPacket(t.second)
        if (a <= b) result += i + 1
    }
    println(result)
}
