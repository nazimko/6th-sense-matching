package com.mhmtn.a6thsense.core.domain.matching

import com.mhmtn.a6thsense.core.domain.Option

object MatchingEngine {

    fun calculate(
        current: List<Option>,
        other: List<String>
    ): Int {
        if (current.isEmpty() || other.isEmpty()) return 0

        val minSize = minOf(current.size, other.size)
        var match = 0

        repeat(minSize) { index ->
            if (current[index].name == other[index]) {
                match++
            }
        }

        return ((match.toFloat() / minSize) * 100).toInt()
    }
}