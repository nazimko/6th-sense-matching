package com.mhmtn.a6thsense.activity.presentation

import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.core.domain.Option

object DailyOptionProvider {

    fun optionsForPhaseAndStep(
        phase: DailyActivityContract.Phase,
        step: Int
    ): List<Option> {
        return when (phase) {
            DailyActivityContract.Phase.PHASE_1 -> phase1OptionsForStep(step)
            DailyActivityContract.Phase.PHASE_2 -> phase2OptionsForStep(step)
            DailyActivityContract.Phase.PHASE_3 -> phase3OptionsForStep(step)
            DailyActivityContract.Phase.PHASE_4 -> phase4OptionsForStep(step)
            DailyActivityContract.Phase.PHASE_5 -> phase5OptionsForStep(step)
            DailyActivityContract.Phase.PHASE_6 -> emptyList()
        }
    }

    // Phase 1: Binary A/B
    private fun phase1OptionsForStep(step: Int): List<Option> = listOf(
        Option.A,
        Option.B
    )

    // Phase 2: Colors (8 step x 4 choices = 32 colors)
    private fun phase2OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.RED, Option.BLUE, Option.GREEN, Option.YELLOW)
            1 -> listOf(Option.PURPLE, Option.ORANGE, Option.PINK, Option.CYAN)
            2 -> listOf(Option.MAGENTA, Option.LIME, Option.TEAL, Option.CORAL)
            3 -> listOf(Option.INDIGO, Option.MINT, Option.LAVENDER, Option.GOLD)
            4 -> listOf(Option.SILVER, Option.BRONZE, Option.EMERALD, Option.RUBY)
            5 -> listOf(Option.SAPPHIRE, Option.AMBER, Option.TURQUOISE, Option.VIOLET)
            6 -> listOf(Option.BEIGE, Option.MAROON, Option.OLIVE, Option.NAVY)
            7 -> listOf(Option.SLATE, Option.CRIMSON, Option.CHARCOAL, Option.IVORY)
            else -> listOf(Option.RED, Option.BLUE, Option.GREEN, Option.YELLOW)
        }
    }

    // Phase 3: Animals (8 step x 4 choices = 32 animals)
    private fun phase3OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.LION, Option.EAGLE, Option.DOLPHIN, Option.WOLF) // Powerful
            1 -> listOf(Option.TIGER, Option.OWL, Option.BUTTERFLY, Option.BEAR) // Intelligent/Graceful
            2 -> listOf(Option.HORSE, Option.SNAKE, Option.PHOENIX, Option.FOX) // Mystic
            3 -> listOf(Option.DRAGON, Option.WHALE, Option.HAWK, Option.PEACOCK) // Majestic
            4 -> listOf(Option.CAT, Option.DOG, Option.RABBIT, Option.DEER) // Gentle
            5 -> listOf(Option.SHARK, Option.OCTOPUS, Option.RAY, Option.TURTLE) // Marine
            6 -> listOf(Option.BAT, Option.SCORPION, Option.SPIDER, Option.RAVEN) // Dark/Nocturnal
            7 -> listOf(Option.SWAN, Option.HUMMINGBIRD, Option.KOALA, Option.PANDA) // Peace/Calm
            else -> listOf(Option.LION, Option.EAGLE, Option.DOLPHIN, Option.WOLF)
        }
    }

    // Phase 4: Elements & Nature (8 step x 4 choices = 32 nature elements)
    private fun phase4OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.FIRE, Option.WATER, Option.EARTH, Option.AIR) // Basic
            1 -> listOf(Option.LIGHTNING, Option.ICE, Option.FOREST, Option.DESERT) // Forces
            2 -> listOf(Option.MOUNTAIN, Option.OCEAN, Option.VOLCANO, Option.WIND) // Landmarks
            3 -> listOf(Option.RAIN, Option.SNOW, Option.SUN, Option.MOON) // Celestial
            4 -> listOf(Option.STORM, Option.THUNDER, Option.CLOUD, Option.FOG) // Weather
            5 -> listOf(Option.RIVER, Option.LAKE, Option.CAVE, Option.CANYON) // Terrain
            6 -> listOf(Option.STAR, Option.GALAXY, Option.NEBULA, Option.COMET) // Deep Space
            7 -> listOf(Option.LEAF, Option.FLOWER, Option.ROOT, Option.SEED) // Organic
            else -> listOf(Option.FIRE, Option.WATER, Option.EARTH, Option.AIR)
        }
    }

    // Phase 5: Dimensions & Abstract (8 step x 4 choices = 32 abstract concepts)
    private fun phase5OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.LIGHT, Option.DARK, Option.TIME, Option.SPACE) // Fundamentals
            1 -> listOf(Option.ENERGY, Option.GRAVITY, Option.INFINITY, Option.VOID) // Physics
            2 -> listOf(Option.COSMOS, Option.QUANTUM, Option.DIMENSION, Option.PARALLEL) // Theoretical
            3 -> listOf(Option.PAST, Option.FUTURE, Option.PRESENT, Option.ETERNITY) // Temporal
            4 -> listOf(Option.MIND, Option.SOUL, Option.SPIRIT, Option.BODY) // Existential
            5 -> listOf(Option.DREAM, Option.REALITY, Option.TRUTH, Option.ILLUSION) // Perception
            6 -> listOf(Option.ORDER, Option.CHAOS, Option.HARMONY, Option.DISCORD) // Universal
            7 -> listOf(Option.WISDOM, Option.KNOWLEDGE, Option.INSTINCT, Option.REASON) // Mental
            else -> listOf(Option.LIGHT, Option.DARK, Option.TIME, Option.SPACE)
        }
    }
}
