import java.io.File

infix fun Int.bitOn(k: Int) = this or (1 shl k)
infix fun Int.bitOff(k: Int) = this and (1 shl k).inv()
fun Int.bit(k: Int) = (this shr k) and 1 == 1
infix fun Int.isBit(k: Int) = this.bit(k)

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

    // dp[v][u][s]: t is time, v is current valve, s is set of opened valves
    fun genEmptyDp() = List(n) {
        List(n) {
            MutableList(1 shl k) { Int.MIN_VALUE }
        }
    }

    var dp = genEmptyDp()
    // t=0, AA, all valves off
    dp[0][0][0] = 0

    val tLimit = 26
    repeat(tLimit) {
        val nextDp = genEmptyDp()
        fun relax(v: Int, u: Int, s: Int, value: () -> Int) {
            value().let { if (nextDp[v][u][s] < it) nextDp[v][u][s] = it }
        }
        for (v in 0 until n) {
            for (u in 0 until n) {
                for (s in 0 until (1 shl k)) {
                    val nextValue = dp[v][u][s] + flow[s]
                    for(vv in graph[v] + v) {
                        for(uu in graph[u] + u) {
                            relax(vv, uu, s) { nextValue }
                        }
                    }
                    interestingMap[v]?.let { iv ->
                        if(!s.bit(iv)) for(uu in graph[u] + u) relax(iv, uu, s bitOn iv) { nextValue }
                        interestingMap[u]?.let { iu ->
                            if(!s.bit(iu)) relax(iv, iu, (s bitOn iu) bitOn iv) { nextValue }
                        }
                    }
                    interestingMap[u]?.let { iu ->
                        if(!s.bit(iu)) for(vv in graph[v] + v) relax(vv, iu, s bitOn iu) { nextValue }
                    }
                }
            }
        }
        dp = nextDp
    }

    val result = dp.maxOf { it.maxOf { it.maxOf { it } } }
    println(result)
}