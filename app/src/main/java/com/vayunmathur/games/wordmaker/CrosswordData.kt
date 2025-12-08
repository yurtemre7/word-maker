package com.vayunmathur.games.wordmaker

import android.content.Context
import java.io.IOException

data class CrosswordData(
    val solutionWords: Set<String>,
    val lettersInChooser: List<Char>,
    val gridStructure: List<String>,
    val letterPositions: Map<String, List<Pair<Int, Int>>>) {

    fun getWordAt(row: Int, col: Int, foundWords: Set<String>): String? {
        // Prioritize vertical words
        for ((word, positions) in letterPositions) {
            if (word !in foundWords) continue
            if (positions.contains(Pair(row, col))) {
                val isVertical = positions.first().second == positions.last().second
                if (isVertical) {
                    return word
                }
            }
        }
        // If no vertical word, check for horizontal
        for ((word, positions) in letterPositions) {
            if(word !in foundWords) continue
            if (positions.contains(Pair(row, col))) {
                return word
            }
        }
        return null
    }

    companion object {
        fun fromAsset(context: Context, fileName: String): CrosswordData? {
            return try {
                val lines = context.assets.open(fileName).bufferedReader().use { it.readLines() }
                val grid = lines.map { it.replace(' ', '.') }
                val (words, positions) = extractWordsAndPositions(grid)
                val chooserLetters = words.flatMap { it.toList() }.distinct().sorted()

                CrosswordData(
                    solutionWords = words.toSet(),
                    lettersInChooser = chooserLetters,
                    gridStructure = grid,
                    letterPositions = positions
                )
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        private fun extractWordsAndPositions(grid: List<String>): Pair<List<String>, Map<String, List<Pair<Int, Int>>>> {
            val words = mutableListOf<String>()
            val positions = mutableMapOf<String, List<Pair<Int, Int>>>()
            val numRows = grid.size
            if (numRows == 0) return Pair(emptyList(), emptyMap())
            val numCols = grid[0].length

            // Horizontal words
            for (r in 0 until numRows) {
                var currentWord = ""
                var startCol = -1
                for (c in 0 until numCols) {
                    val char = grid[r][c]
                    if (char != '.') {
                        if (currentWord.isEmpty()) startCol = c
                        currentWord += char
                    } else {
                        if (currentWord.length > 1) {
                            words.add(currentWord)
                            positions[currentWord] = List(currentWord.length) { i -> Pair(r, startCol + i) }
                        }
                        currentWord = ""
                    }
                }
                if (currentWord.length > 1) {
                    words.add(currentWord)
                    positions[currentWord] = List(currentWord.length) { i -> Pair(r, startCol + i) }
                }
            }

            // Vertical words
            for (c in 0 until numCols) {
                var currentWord = ""
                var startRow = -1
                for (r in 0 until numRows) {
                    val char = grid[r][c]
                    if (char != '.') {
                        if (currentWord.isEmpty()) startRow = r
                        currentWord += char
                    } else {
                        if (currentWord.length > 1) {
                            words.add(currentWord)
                            positions[currentWord] = List(currentWord.length) { i -> Pair(startRow + i, c) }
                        }
                        currentWord = ""
                    }
                }
                if (currentWord.length > 1) {
                    words.add(currentWord)
                    positions[currentWord] = List(currentWord.length) { i -> Pair(startRow + i, c) }
                }
            }

            return Pair(words, positions)
        }
    }
}