package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.exception.ParserInitializationException
import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private class RepeatCombinator<T>(
    private val parser: OrdinaryParser<T>,
    private val atLeast: Int,
    private val atMost: Int
) : OrdinaryParser<List<T>> {
    init {
        if (atLeast < 0) {
            throw ParserInitializationException("Cannot repeat parser $atLeast times, it should be non-negative.")
        }
        if (atMost < atLeast) {
            throw ParserInitializationException(
                "'atLeast=$atLeast' cannot be greater than 'atMost=$atMost' in Repeat-combinator."
            )
        }
    }

    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<List<T>> {
        val values = mutableListOf<T>()
        var currentFromIndex = fromIndex
        while (values.size < atMost) {
            when (val result = parser.parse(tokenProducer, currentFromIndex)) {
                is SuccessfulParse -> {
                    values += result.value
                    currentFromIndex = result.nextTokenIndex
                }
                is ParseFailure -> {
                    return if (values.size >= atLeast) ParsedValue(values, currentFromIndex) else result
                }
            }
        }
        return ParsedValue(values, currentFromIndex)
    }
}

/**
 * @return [OrdinaryParser] that invokes [this] over and over again until it fails. Allows zero successful parses.
 * Puts all results into a list and returns it.
 */
public fun <T> OrdinaryParser<T>.zeroOrMore(): OrdinaryParser<List<T>> = many(0)

/**
 * @return [SkipParser] that invokes [this] over and over again until it fails. Allows zero successful parses.
 * Puts all results into a list and returns it.
 */
public fun <T> SkipParser<T>.zeroOrMore(): SkipParser<List<T>> = many(0)

/**
 * @return [OrdinaryParser] that invokes [this] over and over again until it fails. Doesn't allow zero successful parses.
 * Puts all results into a list and returns it.
 */
public fun <T> OrdinaryParser<T>.oneOrMore(): OrdinaryParser<List<T>> = many(1)

/**
 * @return [SkipParser] that invokes [this] over and over again until it fails. Doesn't allow zero successful parses.
 * Puts all results into a list and returns it.
 */
public fun <T> SkipParser<T>.oneOrMore(): SkipParser<List<T>> = many(1)

/**
 * @return [OrdinaryParser] that invokes [this] until it fails or the number of invokes is at least [atMost].
 * If the number of successful parses is lower than [atLeast], then it returns the failure of the next invoked [this].
 * Otherwise, puts all results into a list and returns it.
 */
public fun <T> OrdinaryParser<T>.many(atLeast: Int, atMost: Int = Int.MAX_VALUE): OrdinaryParser<List<T>> {
    return RepeatCombinator(this, atLeast, atMost)
}

/**
 * @return [SkipParser] that invokes [this] until it fails or the number of invokes is at least [atMost].
 * If the number of successful parses is lower than [atLeast], then it returns the failure of the next invoked [this].
 * Otherwise, puts all results into a list and returns it.
 */
public fun <T> SkipParser<T>.many(atLeast: Int, atMost: Int = Int.MAX_VALUE): SkipParser<List<T>> {
    return RepeatCombinator(inner, atLeast, atMost).skip()
}
