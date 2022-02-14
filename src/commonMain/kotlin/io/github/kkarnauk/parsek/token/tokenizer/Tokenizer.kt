package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.exception.TokenizeException
import io.github.kkarnauk.parsek.info.Location
import io.github.kkarnauk.parsek.token.Token
import io.github.kkarnauk.parsek.token.TokenProducer
import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Required to transform a text into [TokenProducer].
 */
public interface Tokenizer {
    /**
     * Transforms [input] into [TokenProducer].
     */
    public fun tokenize(input: CharSequence): TokenProducer
}

/**
 * * On each step looks for the token from [tokenTypes] with the best match (see [findBestMatch]).
 * * Doesn't return tokens with [TokenType.ignored] `= true`.
 */
public abstract class BestMatchTokenizer(protected val tokenTypes: List<TokenType>) : Tokenizer {
    init {
        require(tokenTypes.isNotEmpty()) { "Tokens types must be non-empty." }
    }

    override fun tokenize(input: CharSequence): TokenProducer = object : TokenProducer {
        val state = State()

        override fun nextToken(): Token? = nextNotIgnoredToken(input, state)
    }

    /**
     * @return the best matched token from [tokenTypes] and the length of the matched segment.
     * For example, 'the best match' can be the longest or the first match.
     * Matching starts from [offset] in [input].
     *
     * If nothing is matched, then `null` should be returned.
     */
    protected abstract fun findBestMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>?

    private fun nextToken(input: CharSequence, state: State): Token? {
        while (true) {
            if (state.offset >= input.length) {
                return null
            }

            val bestMatch = findBestMatch(input, state.offset)
            if (bestMatch == null) {
                throw TokenizeException(
                    "Cannot tokenize the whole input. Unknown token: row=${state.row}, column=${state.column}."
                )
            }

            val (type, length) = bestMatch
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
