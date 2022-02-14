package io.github.kkarnauk.parsek.token.tokenizer.provider

import io.github.kkarnauk.parsek.token.tokenizer.FirstMatchTokenizer
import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides the [FirstMatchTokenizer].
 */
public val firstMatchTokenizerProvider: TokenizerProvider<FirstMatchTokenizer> = run {
    object : TokenizerProvider<FirstMatchTokenizer> {
        override fun provide(tokenTypes: List<TokenType>): FirstMatchTokenizer = FirstMatchTokenizer(tokenTypes)
    }
}
