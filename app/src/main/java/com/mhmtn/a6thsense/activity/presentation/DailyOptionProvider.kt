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

    // Phase 2: Colors (her step farklı renk paleti)
    private fun phase2OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.RED, Option.BLUE, Option.GREEN, Option.YELLOW)
            1 -> listOf(Option.PURPLE, Option.ORANGE, Option.PINK, Option.CYAN)
            2 -> listOf(Option.MAGENTA, Option.LIME, Option.TEAL, Option.CORAL)
            3 -> listOf(Option.INDIGO, Option.MINT, Option.LAVENDER, Option.GOLD)
            else -> listOf(Option.RED, Option.BLUE, Option.GREEN, Option.YELLOW)
        }
    }

    // Phase 3: Animals (her step farklı hayvan kategorisi)
    private fun phase3OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.LION, Option.EAGLE, Option.DOLPHIN, Option.WOLF) // Güçlü hayvanlar
            1 -> listOf(
                Option.TIGER,
                Option.OWL,
                Option.BUTTERFLY,
                Option.BEAR
            ) // Akıllı/zarif hayvanlar
            2 -> listOf(Option.HORSE, Option.SNAKE, Option.PHOENIX, Option.FOX) // Mistik hayvanlar
            3 -> listOf(
                Option.DRAGON,
                Option.WHALE,
                Option.HAWK,
                Option.PEACOCK
            ) // Efsanevi hayvanlar
            else -> listOf(Option.LION, Option.EAGLE, Option.DOLPHIN, Option.WOLF)
        }
    }

    // Phase 4: Elements & Nature (her step farklı doğa kategorisi)
    private fun phase4OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.FIRE, Option.WATER, Option.EARTH, Option.AIR) // Klasik elementler
            1 -> listOf(Option.LIGHTNING, Option.ICE, Option.FOREST, Option.DESERT) // Doğa güçleri
            2 -> listOf(
                Option.MOUNTAIN,
                Option.OCEAN,
                Option.VOLCANO,
                Option.WIND
            ) // Coğrafi güçler
            3 -> listOf(Option.RAIN, Option.SNOW, Option.SUN, Option.MOON) // Gök cisimleri
            else -> listOf(Option.FIRE, Option.WATER, Option.EARTH, Option.AIR)
        }
    }

    // Phase 5: Dimensions & Abstract (her step farklı soyut kavram)
    private fun phase5OptionsForStep(step: Int): List<Option> {
        return when (step) {
            0 -> listOf(Option.LIGHT, Option.DARK, Option.TIME, Option.SPACE) // Temel boyutlar
            1 -> listOf(
                Option.ENERGY,
                Option.GRAVITY,
                Option.INFINITY,
                Option.VOID
            ) // Fiziksel kavramlar
            2 -> listOf(
                Option.COSMOS,
                Option.QUANTUM,
                Option.DIMENSION,
                Option.PARALLEL
            ) // İleri kavramlar
            3 -> listOf(
                Option.PAST,
                Option.FUTURE,
                Option.PRESENT,
                Option.ETERNITY
            ) // Zaman kavramları
            else -> listOf(Option.LIGHT, Option.DARK, Option.TIME, Option.SPACE)
        }
    }
}
