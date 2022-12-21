import java.io.File

infix fun Int.bitOn(k: Int) = this or (1 shl k)
infix fun Int.bitOff(k: Int) = this and (1 shl k).inv()
fun Int.bit(k: Int) = (this shr k) and 1 == 1

fun main() {
    data class ValveData(val name: String, val flowRate: Int, val neighbors: List<String>)

    val valves = File("in.txt").readLines().map { line ->
        val regex = "Valve (\\w\\w) has flow rate=(\\d+); tunnels? leads? to valves? ".toRegex()
        val (toRemove, valveName, flowRateStr) = regex.matchAt(line, 0)!!.groupValues
        val neighbors = line.removePrefix(toRemove).split(", ")
        ValveData(valveName, flowRateStr.toInt(), neighbors)
    }.sortedBy { it.name } // "AA" is first

    val n = valves.size
    val graph = valves.map { v -> v.neighbors.map { u -> valves.indexOfFirst { it.name == u } } }
    val interestingIndices = graph.indices.filter { valves[it].flowRate > 0 }
    val k = interestingIndices.size
    val interestingMap = graph.indices.associateWith { interestingIndices.indexOf(it) }.filterValues { it != -1 }

    val flow = List(1 shl k) { s ->
        interestingIndices.slice((0 until k).filter { s.bit(it) }).sumOf { valves[it].flowRate }
    }

    // dp[t][v][s]: t is time, v is current valve, s is set of opened valves
    val dp = List(31) {
        List(n) {
            MutableList(1 shl k) { Int.MIN_VALUE }
        }
    }
    // t=0, AA, all valves off
    dp[0][0][0] = 0

    for (t in 0 until 30) {
        fun relax(v: Int, s: Int, value: () -> Int) {
            value().let { if (dp[t + 1][v][s] < it) dp[t + 1][v][s] = it }
        }
        for (v in 0 until n) {
            for (s in 0 until (1 shl k)) {
                val nextValue = dp[t][v][s] + flow[s]
                for (u in graph[v]) relax(u, s) { nextValue }
                interestingMap[v]?.let { iv ->
                    if (!s.bit(iv)) relax(v, s bitOn iv) { nextValue }
                }
                relax(v, s) { nextValue }
            }
        }
    }

    val result = dp[30].maxOf { it.maxOf { it } }
    println(result)
}