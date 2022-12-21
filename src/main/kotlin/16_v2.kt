import java.io.File

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

    val interestingValves = listOf("AA") + graph.filter { (_, data) -> data.flowRate > 0 }.keys.toList()
    val n = interestingValves.size
    val neighbors = interestingValves.map { v ->
        graph[v]!!.neighbors.filter { it in interestingValves }.map { interestingValves.indexOf(it) }
    }
    val iDist = (0 until n).map { v ->
        (0 until n).map { u ->
            dist[interestingValves[v]]!![interestingValves[u]]!!
        }
    }

    // dp[t][v][S]
    val dp = List(31) {
        List(n) {
            MutableList(1 shl n) { Int.MIN_VALUE }
        }
    }
    for (t in 0..30) dp[t][0][1] = 0 // stay in "AA"

    fun flow(s: Int) = (0 until n).filter { ((s shr it) and 1) == 1 }.sumOf { graph[interestingValves[it]]!!.flowRate }

    for (t in 1..30) {
        println("calculating t=$t")
        for (v in 0 until n) {
            for (s in 0 until (1 shl n)) {
                val comeFromU = neighbors[v]
                    .filter { u -> t - iDist[u][v] >= 0 }
                    .maxOfOrNull { u -> dp[t - iDist[u][v]][u][s] + iDist[u][v] * flow(s) } ?: Int.MIN_VALUE
                val openV = if (((s shr v) and 1) == 1) { // if v is still closed
                    val sWithoutV = s - (1 shl v)
                    dp[t - 1][v][sWithoutV] + flow(sWithoutV)
                } else Int.MIN_VALUE
                val idle = dp[t - 1][v][s] + flow(s)
                dp[t][v][s] = maxOf(comeFromU, openV, idle)
            }
        }
//        println("\n--- after t=$t ---")
//        println("    \t" + interestingValves.joinToString(separator = "\t"))
//        for (s in 0 until (1 shl n)) {
//            print("s=$s:\t")
//            for (v in 0 until n) print("${dp[t][v][s].takeUnless { it < 0 } ?: '.'}\t")
//            println()
//        }
    }
    val result = dp[30].maxOf { it.maxOf { it } }
    println(result)
}