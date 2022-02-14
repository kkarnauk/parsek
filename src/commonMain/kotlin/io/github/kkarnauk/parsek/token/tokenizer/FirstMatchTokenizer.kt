package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * * On each step looks for the first matched token.
 * * Doesn't return tokens with [TokenType.ignored] ` = true`.
 */
public class FirstMatchTokenizer(tokenTypes: List<TokenType>) : BestMatchTokenizer(tokenTypes) {
    override fun findBestMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>? {
        return tokenTypes.asSequence().mapNotNull { type ->
            val matchedLength = type.match(input, offset)
            if (matchedLength > 0) type to matchedLength else null
        }.firstOrNull()
    }
}
