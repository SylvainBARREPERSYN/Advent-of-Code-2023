private data class SymbolPosition(
    val symbol: Char,
    val abscissa: Int,
    val ordinate: Int
)

private data class Number(
    val value: Int,
    val abscissas: IntRange,
    val ordinate: Int
)

private class EngineSchematic(
    private val symbols: List<SymbolPosition>,
    private val numbers: List<Number>
) {

    val parts: Int get() = partNumbers().sum()

    val gearRatios: Int get() = gears().sumOf { it[0].value * it[1].value }

    private fun gears() = symbols
        .filter {
            it.symbol == '*'
        }.map {
            val gearNumbers = mutableListOf<Number>()
            for (number in numbers) {
                if (it.touch(number)) {
                    gearNumbers.add(number)
                }
            }
            gearNumbers
        }.filter {
            it.size == 2
        }

    private fun partNumbers(): List<Int> {
        val partNumbers = mutableListOf<Int>()
        for (number in numbers) {
            if (symbols.any { it.touch(number) }) {
                partNumbers.add(number.value)
            }
        }
        return partNumbers.toList()
    }

    private fun SymbolPosition.touch(number: Number) =
        number.ordinate in ordinate - 1..ordinate + 1
                && number.abscissas.any { it in abscissa - 1..abscissa + 1 }

    companion object {
        fun of(engineSchematic: List<String>): EngineSchematic {
            val symbols = mutableListOf<SymbolPosition>()
            val numbers = mutableListOf<Number>()
            for ((rowIndex, row) in engineSchematic.withIndex()) {
                var digitStartingPosition = -1
                for ((columnIndex, char) in row.withIndex()) {
                    if (char.isDigit() && digitStartingPosition == -1) {
                        digitStartingPosition = columnIndex
                    } else {
                        if (!char.isDigit() && digitStartingPosition != -1) {
                            numbers.add(
                                Number(
                                    row.substring(digitStartingPosition, columnIndex).toInt(),
                                    digitStartingPosition until columnIndex,
                                    rowIndex
                                )
                            )
                            digitStartingPosition = -1
                        }

                        if (!char.isDigit() && char != '.') {
                            symbols.add(SymbolPosition(char, columnIndex, rowIndex))
                        }
                    }
                }
                if (digitStartingPosition != -1) {
                    numbers.add(
                        Number(
                            row.substring(digitStartingPosition).toInt(),
                            digitStartingPosition until row.length,
                            rowIndex
                        )
                    )
                }
            }
            return EngineSchematic(symbols, numbers)
        }
    }
}

fun main() {
    fun part1(input: List<String>) = EngineSchematic.of(input).parts

    fun part2(input: List<String>) = EngineSchematic.of(input).gearRatios

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
