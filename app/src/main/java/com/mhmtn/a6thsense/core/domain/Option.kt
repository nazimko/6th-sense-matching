package com.mhmtn.a6thsense.core.domain

enum class Option {
    // Phase 1 - Binary choices (A/B seçimleri için)
    A, B,

    // Phase 2 - Colors (16 renk - 4 step x 4 seçenek)
    RED, BLUE, GREEN, YELLOW,
    PURPLE, ORANGE, PINK, CYAN,
    MAGENTA, LIME, TEAL, CORAL,
    INDIGO, MINT, LAVENDER, GOLD,

    // Phase 3 - Animals (16 hayvan - 4 step x 4 seçenek)
    LION, EAGLE, DOLPHIN, WOLF,
    TIGER, OWL, BUTTERFLY, BEAR,
    HORSE, SNAKE, PHOENIX, FOX,
    DRAGON, WHALE, HAWK, PEACOCK,

    // Phase 4 - Elements & Nature (16 element - 4 step x 4 seçenek)
    FIRE, WATER, EARTH, AIR,
    LIGHTNING, ICE, FOREST, DESERT,
    MOUNTAIN, OCEAN, VOLCANO, WIND,
    RAIN, SNOW, SUN, MOON,

    // Phase 5 - Dimensions & Abstract (16 kavram - 4 step x 4 seçenek)
    LIGHT, DARK, TIME, SPACE,
    ENERGY, GRAVITY, INFINITY, VOID,
    COSMOS, QUANTUM, DIMENSION, PARALLEL,
    PAST, FUTURE, PRESENT, ETERNITY
}
