import java.io.File
import kotlin.math.abs

class UnitedSegments {

    val segs = mutableListOf<IntRange>()

    fun add(start: Int, end: Int) {
        if (segs.isEmpty() || start > segs.last().last) {
            segs.add(start..end)
            return
        }
        if (end < segs.first().first) {
            segs.add(0, start..end)
            return
        }
        val startOrNextIndex = segs.indexOfFirst { start in it || it.first > start }
            .takeUnless { it == -1 } ?: (segs.size - 1)
        val endOrPrevIndex = segs.indexOfLast { end in it || it.last < end }
            .takeUnless { it == -1 } ?: 0
        val actualStart = if (start in segs[startOrNextIndex]) segs[startOrNextIndex].first else start
        val actualEnd = if (end in segs[endOrPrevIndex]) segs[endOrPrevIndex].last else end
        segs.removeAll(segs.subList(startOrNextIndex, endOrPrevIndex + 1))
        segs.add(startOrNextIndex, actualStart..actualEnd)
    }
}

fun main() {
    val inputRegex = Regex("Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.+)")
    val input = File("in.txt").readLines().map { line ->
        val (xs, ys, xb, yb) = inputRegex.matchEntire(line)!!.groupValues.drop(1).map { it.toInt() }
        (xs to ys) to (xb to yb)
    }

    val coordLimit = 4000000
    for (yt in 0..coordLimit) {
        val us = UnitedSegments()
        val occupied = mutableSetOf<Pair<Int, Int>>()
        for ((sensor, beacon) in input) {
            val (xs, ys) = sensor
            val (xb, yb) = beacon
            val dist = abs(xs - xb) + abs(ys - yb)
            // xs-xMin + abs(ys-yt) = dist
            // xMax-xs + abs(ys-yt) = dist
            if (abs(ys - yt) <= dist) {
                val xMin = xs + abs(ys - yt) - dist
                val xMax = dist - abs(ys - yt) + xs
                us.add(xMin, xMax)
            }
            if (yb == yt) occupied.add(xb to yb)
            if (ys == yt) occupied.add(xs to ys)
        }
        val areas = us.segs.filter { it.last >= 0 && it.first <= coordLimit }
        if (areas.size != 1) {
            assert(areas.size == 2)
            assert(areas[0].last + 2 == areas[1].first)
            val xt = areas[0].last + 1
            println("xt=$xt, yt=$yt")
            println(xt.toLong() * coordLimit + yt)
            break
        }
    }
    println("done")
}