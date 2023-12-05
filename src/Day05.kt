@JvmInline
value class Seed(val value: Long) : Comparable<Seed> {
    override fun compareTo(other: Seed) = value.compareTo(other.value)
}

@JvmInline
value class SeedRange(private val range: LongRange) : ClosedRange<Seed>, Iterable<Seed> {
    override val endInclusive: Seed get() = Seed(range.last)
    override val start: Seed get() = Seed(range.first)
    override fun iterator() = SeedIterator(start, endInclusive)

    class SeedIterator(start: Seed, private val endInclusive: Seed) : Iterator<Seed> {
        private var initValue = start.value
        override fun hasNext() = initValue <= endInclusive.value
        override fun next() = Seed(initValue++)
    }
}

@JvmInline
value class Location(val value: Long)

class Section(
    private val converters: List<CategoryConverter>,
) {
    fun destinationFor(source: Long): Long {
        for (converter in converters) {
            val destination = converter.destinationFor(source)
            if (destination != null) {
                return destination
            }
        }
        return source
    }
}

class CategoryConverter(
    private val destination: Long,
    private val source: Long,
    private val length: Long
) {
    private val acceptedSources = source until (source + length)

    fun destinationFor(source: Long): Long? {
        if (source in acceptedSources) {
            return (source - this.source) + destination
        }
        return null
    }

    companion object {
        fun of(categoryConverter: String): CategoryConverter {
            val converterItems = categoryConverter.split(" ")
            return CategoryConverter(
                converterItems[0].toLong(),
                converterItems[1].toLong(),
                converterItems[2].toLong()
            )
        }
    }
}

class Almanac(
    private val seeds: List<SeedRange>,
    private val seedToSoilSection: Section,
    private val soilToFertilizerSection: Section,
    private val fertilizerToWaterSection: Section,
    private val waterToLightSection: Section,
    private val lightToTemperatureSection: Section,
    private val temperatureToHumiditySection: Section,
    private val humidityToLocationSection: Section,
) {
    private fun computeLocationFor(seed: Seed): Location {
        val soil = seedToSoilSection.destinationFor(seed.value)
        val fertilizer = soilToFertilizerSection.destinationFor(soil)
        val water = fertilizerToWaterSection.destinationFor(fertilizer)
        val light = waterToLightSection.destinationFor(water)
        val temperature = lightToTemperatureSection.destinationFor(light)
        val humidity = temperatureToHumiditySection.destinationFor(temperature)
        return Location(humidityToLocationSection.destinationFor(humidity))
    }

    fun minimalLocation(): Location {
        var min = Location(Long.MAX_VALUE)
        for (seed in seeds) {
            for (seedValue in seed) {
                val location = computeLocationFor(seedValue)
                if (location.value < min.value) {
                    min = location
                }
            }
        }
        return min
    }

    companion object {
        fun of(almanac: List<String>, seedRanges: Boolean): Almanac {
            val seedsAsString = almanac[0].split(" ")
            val seeds = mutableListOf<SeedRange>()
            if (seedRanges) {
                for (i in 1 until seedsAsString.size step 2) {
                    val startSeed = seedsAsString[i].toLong()
                    val seedRangeLength = seedsAsString[i + 1].toLong()
                    seeds.add(SeedRange(startSeed until (startSeed + seedRangeLength)))
                }
            } else {
                for (i in 1 until seedsAsString.size) {
                    val seed = seedsAsString[i].toLong()
                    seeds.add(SeedRange(seed..seed))
                }
            }

            fun loadConverters(startIndex: Int): Pair<List<CategoryConverter>, Int> {
                val converters = mutableListOf<CategoryConverter>()
                var index = startIndex
                do {
                    converters.add(CategoryConverter.of(almanac[index]))
                    index++
                } while (index < almanac.size && almanac[index].isNotBlank())
                return Pair(converters, index)
            }

            val (seedToSoilConverters, seadToSoilConvertersEndIndex) = loadConverters(3)
            val (soilToFertilizerConverters, soilToFertilizerConvertersEndIndex) = loadConverters(
                seadToSoilConvertersEndIndex + 2
            )
            val (fertilizerToWaterConverters, fertilizerToWaterConvertersEndIndex) = loadConverters(
                soilToFertilizerConvertersEndIndex + 2
            )
            val (waterToLightConverters, waterToLightConvertersEndIndex) = loadConverters(
                fertilizerToWaterConvertersEndIndex + 2
            )
            val (lightToTemperatureConverters, lightToTemperatureConvertersEndIndex) = loadConverters(
                waterToLightConvertersEndIndex + 2
            )
            val (temperatureToHumidityConverters, temperatureToHumidityConvertersEndIndex) = loadConverters(
                lightToTemperatureConvertersEndIndex + 2
            )
            val (humidityToLocationConverters, _) = loadConverters(temperatureToHumidityConvertersEndIndex + 2)

            return Almanac(
                seeds,
                Section(seedToSoilConverters),
                Section(soilToFertilizerConverters),
                Section(fertilizerToWaterConverters),
                Section(waterToLightConverters),
                Section(lightToTemperatureConverters),
                Section(temperatureToHumidityConverters),
                Section(humidityToLocationConverters),
            )
        }
    }
}

fun main() {
    fun part1(input: List<String>) = Almanac.of(input, false).minimalLocation().value

    fun part2(input: List<String>) = Almanac.of(input, true).minimalLocation().value

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
