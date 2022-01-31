package com.github.kkarnauk.parsek.token.types.suppliers

import com.github.kkarnauk.parsek.token.types.TokenType

public abstract class TokenTypeSupplier<T : TokenType> {
    protected var isIgnored: Boolean = false
        private set

    public abstract fun supply(name: String): T

    public fun ignored(value: Boolean = true): TokenTypeSupplier<T> = apply {
        isIgnored = value
    }
}
