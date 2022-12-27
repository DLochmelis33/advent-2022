import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

private val channels = mutableMapOf<String, Channel<Long>>()

private val String.channel get() = channels.getOrPut(this) { Channel(1) }

fun main() = runBlocking {
    val exprRegex = Regex("(\\w{4}): (\\w{4}) (.) (\\w{4})")
    val valueRegex = Regex("(\\w{4}): (\\d+)")
    File("in.txt").readLines().forEach { line ->
        exprRegex.matchEntire(line)?.let {
            val (name, a, opStr, b) = it.groupValues.drop(1)
            val op: (Long, Long) -> Long = when (opStr) {
                "+" -> Long::plus
                "-" -> Long::minus
                "*" -> Long::times
                "/" -> Long::div
                else -> error("unknown op '$opStr'")
            }
            launch {
                name.channel.send(op(a.channel.receive(), b.channel.receive()))
            }
        } ?: valueRegex.matchEntire(line)?.let {
            val (name, valueStr) = it.groupValues.drop(1)
            launch {
                name.channel.send(valueStr.toLong())
            }
        } ?: error("regex didn't match")
    }
    val result = "root".channel.receive()
    println(result)
}