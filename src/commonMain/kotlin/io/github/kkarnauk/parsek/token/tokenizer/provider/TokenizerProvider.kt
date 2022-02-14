package io.github.kkarnauk.parsek.token.tokenizer.provider

import io.github.kkarnauk.parsek.token.tokenizer.Tokenizer
import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides a specific instance of [Tokenizer] by a list of [TokenType].
 *
 * Required because you cannot access actual token types in a grammar.
 */
public interface TokenizerProvider<out T : Tokenizer> {
    public fun provide(tokenTypes: List<TokenType>): T
}

/**
 * Provides a custom tokenizer by invoking [provider]. Use it if you need a different tokenizer than the standard ones.
 */
public fun <T : Tokenizer> provideTokenizer(provider: (List<TokenType>) -> T): TokenizerProvider<T> {
    return object : TokenizerProvider<T> {
        override fun provide(tokenTypes: List<TokenType>): T = provider(tokenTypes)
    }
}
