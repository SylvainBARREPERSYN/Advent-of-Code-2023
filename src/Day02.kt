enum class Color {
    GREEN,
    RED,
    BLUE
}

class ColorQuantity private constructor(
        val value: Int,
        val color: Color
) {
    companion object {
        fun of(colorQuantity: String): ColorQuantity {
            val (quantityAsString, colorAsString) = colorQuantity.split(" ")
            return ColorQuantity(quantityAsString.toInt(), Color.valueOf(colorAsString.uppercase()))
        }
    }
}

class Draw private constructor(
        val colorQuantities: List<ColorQuantity>
) {
    companion object {
        fun of(draw: String): Draw {
            val colorQuantities = draw.split(", ").map { ColorQuantity.of(it) }
            return Draw(colorQuantities)
        }
    }

    fun quantity(color: Color) = colorQuantities.firstOrNull { it.color == color }?.value ?: 0
}

class Game private constructor(
        val gameId: Int,
        val draws: List<Draw>
) {

    companion object {
        fun of(src: String): Game {
            val (key, value) = src.split(": ")
            val gameId = key.split(' ')[1].toInt()
            val draws = value.split("; ").map { Draw.of(it) }
            return Game(gameId, draws)
        }
    }

    fun minRequiredQuantity(color: Color): Int = draws.maxOfOrNull { it.quantity(color) } ?: 0
}

fun main() {
    fun part1(input: List<String>) = input
            .map { Game.of(it) }
            .filter { it.minRequiredQuantity(Color.RED) <= 12 && it.minRequiredQuantity(Color.GREEN) <= 13 && it.minRequiredQuantity(Color.BLUE) <= 14 }
            .sumOf { it.gameId }

    fun part2(input: List<String>) = input
        .map { Game.of(it) }
        .sumOf { it.minRequiredQuantity(Color.RED) * it.minRequiredQuantity(Color.GREEN) * it.minRequiredQuantity(Color.BLUE) }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
