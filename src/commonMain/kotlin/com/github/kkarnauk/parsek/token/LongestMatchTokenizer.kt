package com.github.kkarnauk.parsek.token

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.types.TokenType

/**
 * * On each step looks for the token with the longest matched part
 * * If there are multiple tokens with the longest match, then the first one is chosen
 * * Doesn't return tokens with [TokenType.ignored] ` = true`
 */
public class LongestMatchTokenizer(private val tokenTypes: List<TokenType>) : Tokenizer {
    init {
        require(tokenTypes.isNotEmpty()) { "Tokens types must be non-empty." }
    }

    override fun tokenize(input: CharSequence): TokenProducer = object : TokenProducer {
        val state = State()

        override fun nextToken(): Token? = nextNotIgnoredToken(input, state)
    }

    private fun nextToken(input: CharSequence, state: State): Token? {
        while (true) {
            if (state.offset >= input.length) {
                return null
            }
            val bestMatch = tokenTypes.mapNotNull { type ->
                val matchedLength = type.match(input, state.offset)
                if (matchedLength > 0) matchedLength to type else null
            }.maxByOrNull { it.first }

            requireNotNull(bestMatch) {
                "Cannot tokenize the whole input. Unknown token: row=${state.row}, column=${state.column}."
            }

            val (length, type) = bestMatch
            val (offset, row, column) = state
            repeat(length) {
                state.advance(input[offset + it])
            }
            if (!type.ignored) {
                return Token(type, input, length, Location(offset, row, column))
            }
        }
    }

    private fun nextNotIgnoredToken(input: CharSequence, state: State): Token? {
        while (true) {
            val next = nextToken(input, state)
            if (next == null) {
                // TODO make clear errors
                require(input.length == state.offset) {
                    "Cannot tokenize the whole input. Unknown token: row=${state.row}, column=${state.column}"
                }
                return null
            } else if (!next.type.ignored) {
                return next
            }
        }
    }

    private data class State(
        var offset: Int = 0,
        var row: Int = 1,
        var column: Int = 1,
    ) {
        fun advance(symbol: Char) {
            if (symbol == '\n') {
                row++
                column = 0
            }
            column++
            offset++
        }
    }
}
