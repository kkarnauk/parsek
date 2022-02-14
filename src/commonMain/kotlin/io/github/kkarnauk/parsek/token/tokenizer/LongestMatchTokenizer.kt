package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * * On each step looks for the token with the longest matched part.
 * * If there are multiple tokens with the longest match, then the first one is chosen.
 * * Doesn't return tokens with [TokenType.ignored] ` = true`.
 */
public class LongestMatchTokenizer(tokenTypes: List<TokenType>) : BestMatchTokenizer(tokenTypes) {
    override fun findBestMatch(input: CharSequence, offset: Int): Pair<TokenType, Int>? {
        return tokenTypes.mapNotNull { type ->
            val matchedLength = type.match(input, offset)
            if (matchedLength > 0) type to matchedLength else null
        }.maxByOrNull { it.second }
    }
}
