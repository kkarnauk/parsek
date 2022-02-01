package com.github.kkarnauk.parsek.token.type.provider

import com.github.kkarnauk.parsek.token.type.TokenType

public abstract class TokenTypeProvider<T : TokenType> {
    protected var isIgnored: Boolean = false
        private set

    public abstract fun provide(name: String): T

    public fun ignored(value: Boolean = true): TokenTypeProvider<T> = apply {
        isIgnored = value
    }
}
