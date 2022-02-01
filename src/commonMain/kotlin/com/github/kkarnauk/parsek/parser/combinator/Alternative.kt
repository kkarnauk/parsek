package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.info.EmptyLocation
import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private class AlternativeCombinator<T>(
    val parsers: List<OrdinaryParser<T>>
) : OrdinaryParser<T> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
        val failures = mutableListOf<ParseFailure>()
        for (parser in parsers) {
            when (val result = parser.parse(tokenProducer, fromIndex)) {
                is SuccessfulParse -> return result
                is ParseFailure -> failures += result
            }
        }
        return NoSuchAlternativeFailure(tokenProducer.getOrNull(fromIndex)?.location ?: EmptyLocation, failures)
    }
}

/**
 * @return [OrdinaryParser] that at the start tries to parse with the first parser.
 * If the first one fails, then it uses the second one.
 * If all the parsers fail, then [NoSuchAlternativeFailure] will be returned with the corresponding failures
 * from the first and the second parsers.
 *
 * You can combine more than two parsers with [alt]. Then all parsers will be tried one by one in a similar way.
 */
public infix fun <T> OrdinaryParser<T>.alt(other: OrdinaryParser<T>): OrdinaryParser<T> = when {
    this is AlternativeCombinator -> when (other) {
        is AlternativeCombinator -> AlternativeCombinator(parsers + other.parsers)
        else -> AlternativeCombinator(parsers + other)
    }
    other is AlternativeCombinator -> AlternativeCombinator(other.parsers.toMutableList().also { it.add(0, this) })
    else -> AlternativeCombinator(listOf(this, other))
}

/**
 * @return [SkipParser] that at the start tries to parse with the first parser.
 * If the first one fails, then it uses the second one.
 * If all the parsers fail, then [NoSuchAlternativeFailure] will be returned with the corresponding failures
 * from the first and the second parsers.
 *
 * You can combine more than two parsers with [alt]. Then all parsers will be tried one by one in a similar way.
 */
public infix fun <T> SkipParser<T>.alt(other: SkipParser<T>): SkipParser<T> = (inner alt other.inner).skip()
