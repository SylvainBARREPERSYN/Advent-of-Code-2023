import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16).padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/**
 * Returns the first decimal digit in the char sequence.
 */
fun CharSequence.firstDigit(): Int = first(Char::isDigit).digitToInt()

/**
 * Returns the last decimal digit in the char sequence.
 */
fun CharSequence.lastDigit(): Int = last(Char::isDigit).digitToInt()

/**
 * Map of digit strings to their integer values.
 */
private val DIGITS_AS_STRINGS = mapOf(
    "zero" to 0,
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9
)

/**
 * Regex that matches a single digit or a string representation of a single digit.
 */
private val DIGIT_OR_DIGIT_AS_STRING_REPRESENTATION_REGEX = Regex("[0-9]|${DIGITS_AS_STRINGS.keys.joinToString("|")}")

/**
 * Returns the first decimal digit or a string representation of a decimal digit in the char sequence.

 */
fun CharSequence.firstDigitOrDigitRepresentation(): Int =
    DIGIT_OR_DIGIT_AS_STRING_REPRESENTATION_REGEX.find(this)?.value?.digitOrDigitRepresentationToInt()
        ?: throw IllegalArgumentException("CharSequence $this does not contain a decimal digit or a representation of a decimal digit")

/**
 * Returns the last decimal digit or a string representation of a decimal digit in the char sequence.

 */
fun CharSequence.lastDigitOrDigitRepresentation(): Int {

    // We can't use last of DIGIT_OR_DIGIT_AS_STRING_REPRESENTATION_REGEX.findAll()
    // cause we can have `oneight` and in this case it will return `one` instead of `eight`.

    var result: Int? = null
    var index = length
    while (result == null && index > 0) {
        val substr = substring(--index)
        if (substr[0].isDigit()) {
            result = substr[0].digitToInt()
        } else {
            val digitAsString = DIGITS_AS_STRINGS.keys.firstOrNull { substr.startsWith(it) }
            if (digitAsString != null) {
                result = DIGITS_AS_STRINGS[digitAsString]
            }
        }
    }
    return result
        ?: throw IllegalArgumentException("CharSequence $this does not contain a decimal digit or a representation of a decimal digit")
}

/**
 * Converts a single digit or a string representation of a single digit to an integer.
 */
fun CharSequence.digitOrDigitRepresentationToInt(): Int = when {
    length == 1 && get(0).isDigit() -> get(0).digitToInt()
    DIGITS_AS_STRINGS.containsKey(this) -> DIGITS_AS_STRINGS[this]!!
    else -> throw IllegalArgumentException("CharSequence $this is not a decimal digit or a representation of a decimal digit")
}