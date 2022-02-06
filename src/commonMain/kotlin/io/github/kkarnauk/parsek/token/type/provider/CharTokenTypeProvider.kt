package io.github.kkarnauk.parsek.token.type.provider

import io.github.kkarnauk.parsek.token.type.CharTokenType

/**
 * Provides with [CharTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [CharTokenType].
 */
public fun char(
    char: Char,
    ignoreCase: Boolean = false,
    ignored: Boolean = false
): TokenTypeProvider<CharTokenType> = object : TokenTypeProvider<CharTokenType>() {
    override fun provide(name: String): CharTokenType = CharTokenType(char, ignoreCase, name, isIgnored)
}.ignored(ignored)
