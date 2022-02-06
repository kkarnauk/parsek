package io.github.kkarnauk.parsek.token.type.provider

import io.github.kkarnauk.parsek.token.type.CharPredicateTokenType

/**
 * Provides with [CharPredicateTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [CharPredicateTokenType].
 */
public fun chars(
    ignored: Boolean = false,
    predicate: (Char) -> Boolean
): TokenTypeProvider<CharPredicateTokenType> = object : TokenTypeProvider<CharPredicateTokenType>() {
    override fun provide(name: String): CharPredicateTokenType = CharPredicateTokenType(name, isIgnored, predicate)
}.ignored(ignored)
