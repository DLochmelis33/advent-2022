import java.io.File

private enum class ResourceType {
    ORE, CLAY, OBSIDIAN, GEODE
}

private typealias Cost = Map<ResourceType, Int>

private enum class RobotType(val minedResource: ResourceType) {
    ORE(ResourceType.ORE), CLAY(ResourceType.CLAY), OBSIDIAN(ResourceType.OBSIDIAN), GEODE(ResourceType.GEODE)
}

private typealias Blueprint = Map<RobotType, Cost>

private data class State(
    val resources: Map<ResourceType, Int>,
    val robots: Map<RobotType, Int>,
) {
    fun canAfford(cost: Cost) = cost.all { (resource, price) -> (resources[resource] ?: 0) >= price }

    fun buy(robotType: RobotType, blueprint: Blueprint): State {
        val cost = blueprint[robotType]!!
        return State(
            resources.mapValues { (res, quantity) -> (quantity - (cost[res] ?: 0)).also { assert(it >= 0) } },
            robots + (robotType to 1 + (robots[robotType] ?: 0)),
        )
    }

    fun acquireResources() = State(
        resources.toMutableMap().apply {
            robots.forEach { (robot, count) ->
                val res = robot.minedResource
                this[res] = if (res !in this) count else this[res]!! + count
            }
        },
        robots,
    )
}

private fun State.allowedTransitions(blueprint: Blueprint): List<State> {
    val result = RobotType.values()
        .filter { canAfford(blueprint[it]!!) }
        .map { this.acquireResources().buy(it, blueprint) }
        .toMutableList()
    if (result.size < RobotType.values().size) result += this.acquireResources() // idle only if saving res-s for not doable now
    return result
}

private const val TIME_LIMIT = 24

fun main() {
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

    val bestStrategies = blueprints.map { blueprint ->
        val startingState = State(emptyMap(), mapOf(RobotType.ORE to 1))

        fun reachableStates(state: State, minute: Int = 1): Sequence<State> =
            if (minute > TIME_LIMIT) emptySequence() else sequence {
                state.allowedTransitions(blueprint).forEach {
                    yield(it)
                    yieldAll(reachableStates(it, minute + 1))
                }
            }

//        println("all states:")
//        reachableStates(startingState).forEach { println(it) }


        reachableStates(startingState).maxOf { it.resources[ResourceType.GEODE] ?: 0 }.also {
            println("done blueprint $blueprint")
        }
    }
    val result = bestStrategies.withIndex().sumOf { (i, v) -> (i + 1) * v }
    println(result)
}