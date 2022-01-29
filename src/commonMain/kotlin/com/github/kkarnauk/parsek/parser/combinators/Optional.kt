package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private class OptionalCombinator<T>(private val parser: OrdinaryParser<T>) : OrdinaryParser<T?> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): SuccessfulParse<T?> {
        return when (val result = parser.parse(tokenProducer, fromIndex)) {
            is SuccessfulParse -> result
            is ParseFailure -> ParsedValue(null, fromIndex)
        }
    }
}

/**
 * @return [OrdinaryParser] that returns the result of [this] if it succeeds,
 * `null` otherwise (staying at the same index).
 * Note, that [optional]-parser is always successful.
 */
public fun <T> OrdinaryParser<T>.optional(): OrdinaryParser<T?> = when (this) {
    is OptionalCombinator<*> -> this
    else -> OptionalCombinator(this)
}

/**
 * @return [SkipParser] that returns the result of [this] if it succeeds,
 * `null` otherwise (staying at the same index).
 * Note, that [optional]-parser is always successful.
 */
public fun <T> SkipParser<T>.optional(): SkipParser<T?> = when (inner) {
    is OptionalCombinator<*> -> this
    else -> OptionalCombinator(inner).skip()
}
