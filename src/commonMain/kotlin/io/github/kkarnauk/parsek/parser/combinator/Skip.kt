package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.Parser
import io.github.kkarnauk.parsek.parser.SkipParser

/**
 * Transforms a parser that uses its result into a parser that ignores it.
 * The key usage is [seq].
 */
public fun <T> OrdinaryParser<T>.skip(): SkipParser<T> = SkipParser(this)

/**
 * Transforms a parser that uses its result into a parser that ignores it.
 * The key usage is [seq].
 */
public operator fun <T> OrdinaryParser<T>.unaryMinus(): SkipParser<T> = skip()

/**
 * Transforms a parser that ignores its result into a parser that uses it.
 * The key usage is [seq].
 */
public operator fun <T> SkipParser<T>.unaryPlus(): OrdinaryParser<T> = inner

/**
 * Transforms any parser into a parser that uses its result.
 */
public fun <T> Parser<T>.toOrdinary(): OrdinaryParser<T> = when (this) {
    is OrdinaryParser -> this
    is SkipParser -> +this
}
