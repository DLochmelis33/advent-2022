import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

private val channels = mutableMapOf<String, Channel<Long>>()

private val String.fwdChannel get() = channels.getOrPut("$this-fwd") { Channel(1) }
private val String.bwdChannel get() = channels.getOrPut("$this-bwd") { Channel(1) }

private suspend fun String.sendFwd(l: Long) = fwdChannel.run { repeat(5) { this@run.send(l) } }
private suspend fun String.recvFwd(): Long = fwdChannel.receive()
private suspend fun String.sendBwd(l: Long) = bwdChannel.run { repeat(5) { this@run.send(l) } }
private suspend fun String.recvBwd(): Long = bwdChannel.receive()

private typealias Op = (Long, Long) -> Long

private val plus: Op = Long::plus
private val minus: Op = Long::minus
private val times: Op = Long::times
private val div: Op = Long::div

private val Op.inverse
    get() = when (this) {
        plus -> minus
        minus -> plus
        times -> div
        div -> times
        else -> error("no inverse op")
    }

fun main() = runBlocking {
    val exprRegex = Regex("(\\w{4}): (\\w{4}) (.) (\\w{4})")
    val valueRegex = Regex("(\\w{4}): (\\d+)")
    File("in.txt").readLines().forEach { line ->
        exprRegex.matchEntire(line)?.let {
            val (name, a, opStr, b) = it.groupValues.drop(1)
            val op = when (opStr) {
                "+" -> plus
                "-" -> minus
                "*" -> times
                "/" -> div
                else -> error("unknown op '$opStr'")
            }
            if (name == "root") {
                launch { a.sendBwd(b.recvFwd()) }
                launch { b.sendBwd(a.recvFwd()) }
            } else {
                launch { name.sendFwd(op(a.recvFwd(), b.recvFwd())) }
                when(op) {
                    plus, times -> {
                        launch { a.sendBwd(op.inverse(name.recvBwd(), b.recvFwd())) }
                        launch { b.sendBwd(op.inverse(name.recvBwd(), a.recvFwd())) }
                    }
                    minus, div -> {
                        launch { a.sendBwd(op.inverse(name.recvBwd(), b.recvFwd())) }
                        launch { b.sendBwd(op(a.recvFwd(), name.recvBwd())) }
                    }
                }
            }
            return@let
        } ?: valueRegex.matchEntire(line)?.let {
            val (name, valueStr) = it.groupValues.drop(1)
            if (name != "humn") launch { name.sendFwd(valueStr.toLong()) }
            return@let
        } ?: error("regex didn't match")
    }
    val result = "humn".recvBwd()
    println("result: $result")
    cancel()
}