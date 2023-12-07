sealed class Card(private val value: UInt) : Comparable<Card> {
    override fun compareTo(other: Card) = (value - other.value).toInt()
}

data object CardA : Card(13u)
data object CardK : Card(12u)
data object CardQ : Card(11u)
data object CardJ : Card(10u)
data object CardT : Card(9u)
data object Card9 : Card(8u)
data object Card8 : Card(7u)
data object Card7 : Card(6u)
data object Card6 : Card(5u)
data object Card5 : Card(4u)
data object Card4 : Card(3u)
data object Card3 : Card(2u)
data object Card2 : Card(1u)
data object CardJoker : Card(0u)

fun Char.toCard(withJokers: Boolean) = when (this) {
    'A' -> CardA
    'K' -> CardK
    'Q' -> CardQ
    'J' -> if (withJokers) CardJoker else CardJ
    'T' -> CardT
    '9' -> Card9
    '8' -> Card8
    '7' -> Card7
    '6' -> Card6
    '5' -> Card5
    '4' -> Card4
    '3' -> Card3
    '2' -> Card2
    else -> throw IllegalStateException("Char $this is not a valid card")
}

data class Hand(
    val card1: Card,
    val card2: Card,
    val card3: Card,
    val card4: Card,
    val card5: Card,
    val bid: UInt
) : Comparable<Hand> {
    private val cards = listOf(card1, card2, card3, card4, card5)

    private val type = score()

    private fun score(): Type {
        val countByCardWithJokers = this.cards.groupingBy { it }.eachCount()
        val numberOfJokers = countByCardWithJokers[CardJoker] ?: 0
        val countByCard = countByCardWithJokers.filter { it.key != CardJoker }.toMutableMap()
        if (countByCard.keys.size == 0) {
            countByCard[CardJ] = 5
        } else {
            val mostRepresentedCard = countByCard.toList().maxBy { it.second }.first
            countByCard[mostRepresentedCard] = numberOfJokers + countByCard[mostRepresentedCard]!!
        }

        return when {
            countByCard.keys.size == 1 -> Type.FIVE_OF_A_KIND
            countByCard.keys.size == 5 -> Type.HIGH_CARD
            countByCard.keys.size == 4 -> Type.ONE_PAIR
            countByCard.keys.size == 2 && countByCard.values.any { it == 4 } -> Type.FOUR_OF_A_KIND
            countByCard.keys.size == 2 -> Type.FULL_HOUSE
            countByCard.keys.size == 3 && countByCard.values.any { it == 3 } -> Type.THREE_OF_A_KIND
            countByCard.keys.size == 3 -> Type.TWO_PAIR
            else -> throw IllegalStateException()
        }
    }

    override fun compareTo(other: Hand): Int {
        if (type < other.type) {
            return -1
        } else if (type > other.type) {
            return 1
        } else {
            var lowerHand: Hand? = null
            var cardIndex = 0
            do {
                val comparison = cards[cardIndex].compareTo(other.cards[cardIndex])
                if (comparison < 0) {
                    lowerHand = this
                } else if (comparison > 0) {
                    lowerHand = other
                }
                cardIndex++
            } while (lowerHand == null && cardIndex < 5)
            return if (lowerHand == null) 0 else if (lowerHand == this) -1 else 1
        }
    }

    companion object {
        fun of(hand: String, withJokers: Boolean): Hand {
            val (cardsAsString, bidAsString) = hand.split(" ")
            val cards = cardsAsString.toCharArray().map { it.toCard(withJokers) }
            val bid = bidAsString.toUInt()
            return Hand(cards[0], cards[1], cards[2], cards[3], cards[4], bid)
        }
    }

    private enum class Type {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND,
    }
}

private class Score(
    rank: UInt,
    bid: UInt
) {
    val value = rank * bid
}

private data class CamelCardGame(
    private val hands: List<Hand>
) {
    fun score() = hands
        .sorted()
        .mapIndexed { index, hand -> Score(index.toUInt() + 1u, hand.bid) }
        .sumOf { it.value }

    companion object {
        fun of(hands: List<String>, withJokers: Boolean) = CamelCardGame(hands.map { Hand.of(it, withJokers) })
    }
}

fun main() {
    fun part1(input: List<String>) = CamelCardGame.of(input, withJokers = false).score()
    fun part2(input: List<String>) = CamelCardGame.of(input, withJokers = true).score()

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
