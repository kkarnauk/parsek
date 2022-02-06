package io.github.kkarnauk.parsek.token.type.provider

import io.github.kkarnauk.parsek.token.type.PredicateTokenType
import io.github.kkarnauk.parsek.token.type.predicateTokenType
import io.github.kkarnauk.parsek.token.type.TokenType

/**
 * Provides with [PredicateTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [PredicateTokenType].
 * It uses [predicate] instead of [TokenType.match].
 */
public fun tokenType(
    ignored: Boolean = false,
    predicate: (input: CharSequence, fromIndex: Int) -> Int
): TokenTypeProvider<PredicateTokenType> = object : TokenTypeProvider<PredicateTokenType>() {
    override fun provide(name: String): PredicateTokenType = PredicateTokenType(name, isIgnored, predicate)
}.ignored(ignored)

/**
 * Provides with [PredicateTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [PredicateTokenType].
 * It uses [predicate] instead of [TokenType.match].
 *
 * You may assume that `fromIndex` is always 0.
 */
public fun tokenType(
    ignored: Boolean = false,
    predicate: (input: CharSequence) -> Int
): TokenTypeProvider<PredicateTokenType> = object : TokenTypeProvider<PredicateTokenType>() {
    override fun provide(name: String): PredicateTokenType = predicateTokenType(name, isIgnored, predicate)
}.ignored(ignored)
