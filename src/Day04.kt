private class ScratchCard private constructor(
    private val winningNumbers: List<Int>,
    private val numbers: List<Int>
) {
    fun computePoints() = wins().let { wins -> if (wins == 0) 0 else (0 until wins - 1).fold(1) { acc, _ -> acc * 2 } }

    fun wins() = numbers.filter { it in winningNumbers }.size

    companion object {
        fun of(card: String): ScratchCard {
            val (winningNumbersAsString, numbersAsString) = card.split(": ")[1].split("|")
            return ScratchCard(
                winningNumbers = winningNumbersAsString.split(" ").filter { it != "" }.map { it.toInt() },
                numbers = numbersAsString.split(" ").filter { it != "" }.map { it.toInt() }
            )
        }
    }
}

private class ScratchCardGame private constructor(
    private val cards: List<ScratchCard>
) {
    fun computePoints() = cards.sumOf { it.computePoints() }

    fun play(): Int {
        val cards = List(this.cards.size) { index -> (index + 1) to 1 }.toMap().toMutableMap()
        var index = 0
        var wins: Int
        while (index < this.cards.size) {
            val card = this.cards[index]
            wins = card.wins()
            for (i in 1 .. wins) {
                cards[index + i + 1] = cards[index + i + 1]?.plus(cards[index + 1]!!)!!
            }
            index++
        }
        return cards.values.sum()
    }

    companion object {
        fun of(game: List<String>) = ScratchCardGame(game.map { ScratchCard.of(it) })
    }
}

fun main() {
    fun part1(input: List<String>) = ScratchCardGame.of(input).computePoints()

    fun part2(input: List<String>) = ScratchCardGame.of(input).play()

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
