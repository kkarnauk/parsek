package io.github.kkarnauk.parsek.token.tokenizer.provider

import io.github.kkarnauk.parsek.token.tokenizer.LongestMatchTokenizer
import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides the [LongestMatchTokenizer].
 */
public val longestMatchTokenizerProvider: TokenizerProvider<LongestMatchTokenizer> = run {
    object : TokenizerProvider<LongestMatchTokenizer> {
        override fun provide(tokenTypes: List<TokenType>): LongestMatchTokenizer = LongestMatchTokenizer(tokenTypes)
    }
}
