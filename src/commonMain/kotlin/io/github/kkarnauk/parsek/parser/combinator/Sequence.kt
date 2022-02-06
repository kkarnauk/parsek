package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.parser.*
import io.github.kkarnauk.parsek.token.IndexedTokenProducer

// TODO maybe make more efficient by storing list of parsers
private class SequenceCombinator<T, S, R>(
    private val first: OrdinaryParser<T>,
    private val second: OrdinaryParser<S>,
    private val transform: (T, S) -> R
) : OrdinaryParser<R> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<R> {
        return when (val firstResult = first.parse(tokenProducer, fromIndex)) {
            is SuccessfulParse -> when (val secondResult = second.parse(tokenProducer, firstResult.nextTokenIndex)) {
                is SuccessfulParse -> ParsedValue(
                    transform(firstResult.value, secondResult.value),
                    secondResult.nextTokenIndex
                )
                is ParseFailure -> secondResult
            }
            is ParseFailure -> firstResult
        }
    }
}

/**
 * @return [OrdinaryParser] that combines two results of the sequential parsers.
 * If at least one fails, then the whole result is the first failure.
 */
public infix fun <T, S> OrdinaryParser<T>.seq(other: OrdinaryParser<S>): OrdinaryParser<Pair<T, S>> =
    SequenceCombinator(this, other, ::Pair)

/**
 * @return [OrdinaryParser] that takes the first result, applies the second parser and returns the first result.
 * If at least one fails, then the whole result is the first failure.
 */
public infix fun <T, S> OrdinaryParser<T>.seq(other: SkipParser<S>): OrdinaryParser<T> =
    SequenceCombinator(this, other.inner) { x, _ -> x }

/**
 * @return [OrdinaryParser] that applies the first parser, takes the second result and returns it.
 * If at least one fails, then the whole result is the first failure.
 */
public infix fun <T, S> SkipParser<T>.seq(other: OrdinaryParser<S>): OrdinaryParser<S> =
    SequenceCombinator(inner, other) { _, y -> y }

/**
 * @return [SkipParser] that applies two parsers sequentially.
 * If at least one fails, then the whole result is the first failure.
 */
public infix fun <T, S> SkipParser<T>.seq(other: SkipParser<S>): SkipParser<Pair<T, S>> =
    SequenceCombinator(inner, other.inner, ::Pair).skip()
