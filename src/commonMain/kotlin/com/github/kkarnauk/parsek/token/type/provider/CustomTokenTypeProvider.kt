package com.github.kkarnauk.parsek.token.type.provider

import com.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides with any token type [T] you pass to it.
 * Not really required, but may be useful.
 * @see [tokenType]
 */
public fun <T : TokenType> tokenType(type: T): TokenTypeProvider<T> = object : TokenTypeProvider<T>() {
    override fun provide(name: String): T = type
}.ignored(type.ignored)
