import java.io.File

private data class Valve(val name: String, val flowRate: Int, val neighbors: List<String>)

fun main() {
    val valves = File("in.txt").readLines().map { line ->
        val regex = "Valve (\\w\\w) has flow rate=(\\d+); tunnels? leads? to valves? ".toRegex()
        val (toRemove, valveName, flowRateStr) = regex.matchAt(line, 0)!!.groupValues
        val neighbors = line.removePrefix(toRemove).split(", ")
        Valve(valveName, flowRateStr.toInt(), neighbors)
    }
    val graph = valves.map { v -> v.neighbors.map { neigh -> valves.indexOfFirst { it.name == neigh } } }
    val interestingIndices = valves.withIndex().filter { (_, v) -> v.flowRate > 0 }.map { (i, _) -> i }
    val iiMap = graph.indices
        .filter { it in interestingIndices }
        .associateWith { interestingIndices.indexOf(it) }

    val n = valves.size
    val k = interestingIndices.size
    val MINF = -1_000_000_000
    val flow = List(1 shl k) { s ->
        (0 until k).filter { s.bit(it) }.let { interestingIndices.slice(it) }.sumOf { valves[it].flowRate }
    }

    println("size(int) * n * n * (2^k) = ${4 * n * n * (1 shl k)}")

    var dp = List(n) { List(n) { MutableList(1 shl k) { MINF } } }
    println("wait for it")
    val nextDp = List(n) { List(n) { MutableList(1 shl k) { MINF } } }
    println("allocated two dp-s")

    val aaIndex = valves.indexOfFirst { it.name == "AA" }
    dp[aaIndex][aaIndex][0] = 0

    fun relax(v: Int, u: Int, s: Int, value: Int) {
        if (nextDp[v][u][s] < value) nextDp[v][u][s] = value
    }

    repeat(26) {
        println("t=$it: started")
        for (v in 0 until n) {
            for (u in 0 until n) {
                for (s in 0 until (1 shl k)) {
                    val curValue = dp[v][u][s] + flow[s]
                    val transV = graph[v].map { it to s }.toMutableList()
                    iiMap[v]?.let { iv ->
                        if (!(s isBit iv)) transV += v to (s bitOn iv)
                    }
                    transV += v to s
                    val transU = graph[u].map { it to s }.toMutableList()
                    iiMap[u]?.let { iu ->
                        if (!(s isBit iu)) transU += u to (s bitOn iu)
                    }
                    transU += u to s
                    for ((nv, nsv) in transV) {
                        for ((nu, nsu) in transU) {
                            val ns = nsv or nsu
                            relax(nv, nu, ns, curValue)
                        }
                    }
                }
            }
        }
        println("t=$it: copying")
        dp = nextDp.map { it.map { it.toMutableList() } }
        nextDp.forEach { it.forEach { for (i in it.indices) it[i] = MINF } }
    }

    val result = dp.maxOf { it.maxOf { it.maxOf { it } } }
    println("RESULT: $result")
}