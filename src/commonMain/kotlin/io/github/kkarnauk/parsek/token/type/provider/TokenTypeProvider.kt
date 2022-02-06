package io.github.kkarnauk.parsek.token.type.provider

import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides a token type [T] by a name.
 * Required to take names from properties when constructing a grammar and provide with token types.
 */
public abstract class TokenTypeProvider<T : TokenType> {
    protected var isIgnored: Boolean = false
        private set

    public abstract fun provide(name: String): T

    public fun ignored(value: Boolean = true): TokenTypeProvider<T> = apply {
        isIgnored = value
    }
}
