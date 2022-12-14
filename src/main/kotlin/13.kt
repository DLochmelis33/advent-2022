import kotlinx.serialization.json.*
import java.io.File

sealed class PacketStruct : Comparable<PacketStruct>

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
    fun parseToPacket(s: String): PacketStruct {
        fun parseElem(e: JsonElement): PacketStruct = when (e) {
            is JsonPrimitive -> IntPacket(e.int)
            is JsonArray -> ListPacket(e.map { parseElem(it) })
            else -> error("cannot parse $e")
        }
        return parseElem(Json.parseToJsonElement(s))
    }

    val divider1 = parseToPacket("[[2]]")
    val divider2 = parseToPacket("[[6]]")

    val input = File("in.txt").readLines()
        .filter { it.isNotEmpty() }
        .map(::parseToPacket)

    val sorted = (input + listOf(divider1, divider2)).sorted()
    println((sorted.indexOf(divider1) + 1) * (sorted.indexOf(divider2) + 1))
}
