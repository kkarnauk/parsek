package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private class MapCombinator<T, R>(
    private val parser: OrdinaryParser<T>,
    private val transform: (T) -> R
) : OrdinaryParser<R> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<R> {
        return when (val result = parser.parse(tokenProducer, fromIndex)) {
            is SuccessfulParse -> ParsedValue(transform(result.value), result.nextTokenIndex)
            is ParseFailure -> result
        }
    }
}

/**
 * @return [OrdinaryParser] that applies [transform] to the result of [this] if it is successful.
 * Otherwise, it returns the same failure.
 */
public infix fun <T, R> OrdinaryParser<T>.map(transform: (T) -> R): OrdinaryParser<R> =
    MapCombinator(this, transform)

/**
 * @return [SkipParser] that applies [transform] to the result of [this] if it is successful.
 * Otherwise, it returns the same failure.
 */
public infix fun <T, R> SkipParser<T>.map(transform: (T) -> R): SkipParser<R> =
    MapCombinator(inner, transform).skip()
