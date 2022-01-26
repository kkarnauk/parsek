package com.github.kkarnauk.parsek.token.tokenizer

import com.github.kkarnauk.parsek.token.types.TokenType

/**
 * Supplies a specific instance of [Tokenizer] by a list of [TokenType].
 *
 * Required because you cannot access actual token types in a grammar.
 */
public interface TokenizerSupplier<out T : Tokenizer> {
    public fun supply(tokenTypes: List<TokenType>): T
}

/**
 * Supplies the longest match tokenizer.
 */
public val longestMatchTokenizerSupplier: TokenizerSupplier<LongestMatchTokenizer> = run {
    object : TokenizerSupplier<LongestMatchTokenizer> {
        override fun supply(tokenTypes: List<TokenType>): LongestMatchTokenizer = LongestMatchTokenizer(tokenTypes)
    }
}

/**
 * Supplies a custom tokenizer by invoking [supplier]. Use it if you need a different tokenizer than the standard ones.
 */
public fun <T : Tokenizer> supplyTokenizer(supplier: (List<TokenType>) -> T): TokenizerSupplier<T> {
    return object : TokenizerSupplier<T> {
        override fun supply(tokenTypes: List<TokenType>): T = supplier(tokenTypes)
    }
}
