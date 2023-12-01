fun main() {
    fun part1(input: List<String>) = input
        .sumOf { it.firstDigit() * 10 + it.lastDigit() }

    fun part2(input: List<String>) = input
        .sumOf { it.firstDigitOrDigitRepresentation() * 10 + it.lastDigitOrDigitRepresentation() }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
