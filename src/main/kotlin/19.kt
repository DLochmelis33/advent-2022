import kotlinx.coroutines.coroutineScope
import java.io.File

private enum class ResourceType {
    ORE, CLAY, OBSIDIAN, GEODE
}

private typealias Cost = Map<ResourceType, Int>

private enum class RobotType(val minedResource: ResourceType) {
    ORE(ResourceType.ORE), CLAY(ResourceType.CLAY), OBSIDIAN(ResourceType.OBSIDIAN), GEODE(ResourceType.GEODE)
}

private typealias Blueprint = Map<RobotType, Cost>

private data class State19(
    val resources: Map<ResourceType, Int>,
    val robots: Map<RobotType, Int>,
    val minute: Int,
) {
    fun canAfford(cost: Cost) = cost.all { (resource, price) -> (resources[resource] ?: 0) >= price }

    fun buy(robotType: RobotType, blueprint: Blueprint): State19 {
        val cost = blueprint[robotType]!!
        return State19(
            resources.mapValues { (res, quantity) -> (quantity - (cost[res] ?: 0)).also { assert(it >= 0) } },
            robots + (robotType to 1 + (robots[robotType] ?: 0)),
            minute,
        )
    }

    fun acquireResources() = State19(
        resources.toMutableMap().apply {
            robots.forEach { (robot, count) ->
                val res = robot.minedResource
                this[res] = if (res !in this) count else this[res]!! + count
            }
        },
        robots,
        minute + 1
    )
}

private data class BlueprintInfo(val maxOreCost: Int, val maxClayCost: Int)

private val blueprintInfo = mutableMapOf<Blueprint, BlueprintInfo>()

private val Blueprint.info get() = blueprintInfo[this]!!

private data class COCO(
    val minute: Int,
    val p1: Int,
    val p2: Int,
    val p3: Int,
)

private val cocoMajorMap = List(4) { mutableMapOf<COCO, Int>() }

private fun checkMajorant(s: State19): Boolean {
    if (s.resources.size > 2 || s.robots.size > 2) return true

    val oRes = s.resources[ResourceType.ORE] ?: 0
    val cRes = s.resources[ResourceType.CLAY] ?: 0
    val oRob = s.robots[RobotType.ORE] ?: 0
    val cRob = s.robots[RobotType.CLAY] ?: 0
    val m = s.minute

    val oco = COCO(m, oRes, cRes, oRob)
    if ((cocoMajorMap[0][oco] ?: -1) >= cRob) return false else cocoMajorMap[0][oco] = cRob
    val occ = COCO(m, oRes, cRes, cRob)
    if ((cocoMajorMap[1][occ] ?: -1) >= oRob) return false else cocoMajorMap[1][occ] = oRob
    val ooc = COCO(m, oRes, oRob, cRob)
    if ((cocoMajorMap[2][ooc] ?: -1) >= cRes) return false else cocoMajorMap[2][ooc] = cRes
    val coc = COCO(m, cRes, oRob, cRob)
    if ((cocoMajorMap[3][coc] ?: -1) >= oRes) return false else cocoMajorMap[3][coc] = oRes
    return true
}

private fun cleanupOCOC() {
    cocoMajorMap.forEach { it.clear() }
}

private fun State19.allowedTransitions(blueprint: Blueprint): Sequence<State19> {
    if (minute == TIME_LIMIT) return emptySequence()

    // -------- questionable pruning ---------
    val oreQty = resources[ResourceType.ORE] ?: 0

//    if ((robots.size <= 2 && oreQty > blueprint.info.maxOreCost * 2)) return emptySequence()

    if (!checkMajorant(this)) return emptySequence()
    if (canAfford(blueprint[RobotType.GEODE]!!)) { // always buy geode when possible
        return sequenceOf(this.acquireResources().buy(RobotType.GEODE, blueprint))
    }
    val canAffordClayRobot = canAfford(blueprint[RobotType.CLAY]!!)
//    if (minute < 6 && canAffordClayRobot) { // perhaps can build ore later (if necessary)
//        return sequenceOf(this.acquireResources().buy(RobotType.CLAY, blueprint))
//    }
    if (canAffordClayRobot && oreQty > blueprint.info.maxOreCost * 3) {
        return sequenceOf(this.acquireResources().buy(RobotType.CLAY, blueprint))
    }
    // -------- pruning end ---------

    val withRobots = RobotType.values().asSequence()
        .filter { canAfford(blueprint[it]!!) }
        .map { this.acquireResources().buy(it, blueprint) }
    return withRobots + this.acquireResources() // idle
}

private const val TIME_LIMIT = 32

suspend fun main() = coroutineScope {
    val inputRegex = Regex(
        "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian."
    )
    val blueprints = File("in.txt").readLines().map {
        val values = inputRegex.matchEntire(it)!!.groupValues.drop(2).map { it.toInt() } // drop entire and id
        mapOf(
            RobotType.ORE to mapOf(ResourceType.ORE to values[0]),
            RobotType.CLAY to mapOf(ResourceType.ORE to values[1]),
            RobotType.OBSIDIAN to mapOf(ResourceType.ORE to values[2], ResourceType.CLAY to values[3]),
            RobotType.GEODE to mapOf(ResourceType.ORE to values[4], ResourceType.OBSIDIAN to values[5])
        )
    }
    blueprintInfo.putAll(blueprints.associateWith {
        BlueprintInfo(
            it.maxOf { (_, cost) -> cost[ResourceType.ORE] ?: 0 },
            it.maxOf { (_, cost) -> cost[ResourceType.CLAY] ?: 0 }
        )
    })

    val bestStrategies = blueprints.withIndex().take(3).map { (i, blueprint) ->
        val startingState = State19(emptyMap(), mapOf(RobotType.ORE to 1), 0)

        fun State19.reachableStates(): Sequence<State19> = sequence {
            if (minute == TIME_LIMIT) yield(this@reachableStates) else {
                yieldAll(allowedTransitions(blueprint).flatMap { it.reachableStates() })
            }
//            yield(this@reachableStates)
//            yieldAll(allowedTransitions(blueprint).flatMap { it.reachableStates() })
        }

//        startingState.reachableStates()
//            .sortedWith(
//                compareBy<State> { it.minute }
//                    .thenBy { it.robots[RobotType.CLAY] }
//                    .thenBy { it.robots[RobotType.ORE] }
//                    .thenBy { it.resources[ResourceType.CLAY] }
//                    .thenBy { it.resources[ResourceType.ORE] }
//            ).toList()
//            .joinToString("\n")
//            .also { println(it) }

//        val hehe = startingState.reachableStates().take(100).toList().map { s ->
//            async {
//                s.reachableStates().maxBy { it.resources[ResourceType.GEODE] ?: 0 }
//            }
//        }.awaitAll().maxBy { it.resources[ResourceType.GEODE] ?: 0 }

        val bestState = startingState.reachableStates().maxBy { it.resources[ResourceType.GEODE] ?: 0 }
        println("done blueprint #${i + 1}\n\tbest state: $bestState")
        cleanupOCOC()

        bestState.resources[ResourceType.GEODE] ?: 0
    }
    val result = bestStrategies.reduce(Int::times)
    println(result)
}