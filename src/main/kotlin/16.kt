import java.io.File
import java.time.LocalTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

//fun <T> List<T>.permutations(): Sequence<Sequence<T>> = if(isEmpty()) sequenceOf(emptySequence()) else sequence {
//    for ((i, t) in this@permutations.withIndex()) {
//        val remainder = (this@permutations.take(i) + this@permutations.drop(i + 1))
//        remainder.permutations().forEach { yield(listOf(t) + it) }
//    }
//}

inline fun <reified T> List<T>.permutations(): Sequence<List<T>> = sequence {
    val n = this@permutations.size
    val arr = ArrayList(this@permutations)
    val c = MutableList(arr.size) { 0 }

    yield(arr)
    var i = 1
    while (i < n) {
        if (c[i] < i) {
            if (i % 2 == 0) {
                Collections.swap(arr, 0, i)
            } else {
                Collections.swap(arr, c[i], i)
            }
            yield(arr)
            c[i] += 1
            i = 1
        } else {
            c[i] = 0
            i += 1
        }
    }
}

fun main() {
    data class ValveData(val flowRate: Int, val neighbors: List<String>)

    val graph = mutableMapOf<String, ValveData>()
    File("in.txt").readLines().forEach { line ->
        val regex = "Valve (\\w\\w) has flow rate=(\\d+); tunnels? leads? to valves? ".toRegex()
        val (toRemove, valveName, flowRateStr) = regex.matchAt(line, 0)!!.groupValues
        val neighbors = line.removePrefix(toRemove).split(", ")
        graph[valveName] = ValveData(flowRateStr.toInt(), neighbors)
    }
    val valves = graph.keys

    val dist = mutableMapOf<String, MutableMap<String, Int>>()
    for ((v, data) in graph) {
        dist[v] = valves.associateWith { 1_000_000_000 }.toMutableMap()
        dist[v]!![v] = 0
        for (u in data.neighbors) dist[v]!![u] = 1
    }
    for (v in graph.keys)
        for (u in graph.keys)
            for (t in graph.keys) {
                val dvt = dist[v]!![t]!!
                val dtu = dist[t]!![u]!!
                if (dist[v]!![u]!! > dvt + dtu)
                    dist[v]!![u] = dvt + dtu
            }

    val interestingValves = graph.filter { (_, data) -> data.flowRate > 0 }.keys.toList()
    println("interesting valves count: ${interestingValves.size}")
    val totalPermutations = (1L..interestingValves.size).reduce(Long::times)
    var cnt = 0L
    val startTimestamp = System.currentTimeMillis()
    val result = interestingValves.permutations().maxOf { perm ->
        cnt++
        if(cnt % 10_000_000 == 0L) {
            val percentageDone = cnt.toDouble() / totalPermutations
            val elapsedMillis = System.currentTimeMillis() - startTimestamp
            val expectedTotalMillis = (elapsedMillis / percentageDone - elapsedMillis)
                .roundToLong().milliseconds.toIsoString().removePrefix("PT")
            println("Done: $cnt == ${String.format("%.6f", percentageDone)}% of total, ETA: T+$expectedTotalMillis")
        }
        var releasedPressure = 0
        var openFlow = 0
        var time = 0
        for ((v, u) in (listOf("AA") + perm).zipWithNext()) {
            val moveTime = dist[v]!![u]!! + 1 // opening time
            if (time + moveTime > 30) {
                val maxTime = 30 - time
                releasedPressure += openFlow * maxTime
                break
            }
            time += moveTime
            releasedPressure += openFlow * moveTime
            openFlow += graph[u]!!.flowRate
        }
        if (time < 30) releasedPressure += openFlow * (30 - time)

        releasedPressure
    }
    println("RESULT: $result")
}