package com.github.kkarnauk.parsek.token.type.provider

import com.github.kkarnauk.parsek.token.type.LiteralTokenType

/**
 * Provides with [LiteralTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [LiteralTokenType].
 */
public fun literal(
    text: String,
    ignoreCase: Boolean = false,
    ignored: Boolean = false
): TokenTypeProvider<LiteralTokenType> = object : TokenTypeProvider<LiteralTokenType>() {
    override fun provide(name: String): LiteralTokenType = LiteralTokenType(text, ignoreCase, name, isIgnored)
}.ignored(ignored)
