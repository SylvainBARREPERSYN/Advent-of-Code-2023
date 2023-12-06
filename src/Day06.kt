@JvmInline
value class RaceStrategy(val holdingDuration: Int)

data class Race(
    val time: Long,
    val distance: Long
) {
    val strategies: List<RaceStrategy>

    init {
        var holdingDuration = 1
        val strategies = mutableListOf<RaceStrategy>()

        do {
            val speed = holdingDuration
            val distance = speed * (time - holdingDuration)
            if (this.distance < distance) {
                strategies.add(RaceStrategy(holdingDuration))
            }
            holdingDuration++
        } while (holdingDuration < time - 1)
        this.strategies = strategies
    }
}

@JvmInline
value class PaperSheet(
    val races: List<Race>
) {
    fun multiplyStrategiesToBeatRecordsForEachRace(): Int {
        val strategiesForEachRace = races.map { it.strategies.size }
        var result = strategiesForEachRace[0]
        for (i in 1 until strategiesForEachRace.size) {
            result *= strategiesForEachRace[i]
        }
        return result
    }
}

fun main() {
    fun part1(input: List<String>): Int {
        fun readRow(rowIndex: Int): List<Long> {
            return input[rowIndex]
                .split(" ")
                .filter { it.isNotBlank() && it != " " }
                .filter { it.toIntOrNull() != null }
                .map { it.toLong() }
        }

        val times = readRow(0)
        val distances = readRow(1)

        val races = mutableListOf<Race>()
        for (i in times.indices) {
            races.add(Race(times[i], distances[i]))
        }
        return PaperSheet(races).multiplyStrategiesToBeatRecordsForEachRace()
    }


    fun part2(input: List<String>): Int {
        fun readRow(rowIndex: Int): Long {
            return input[rowIndex]
                .replace(" ", "")
                .split(":")[1]
                .toLong()
        }

        val time = readRow(0)
        val distance = readRow(1)
        return Race(time, distance).strategies.size
    }

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
