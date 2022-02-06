package io.github.kkarnauk.parsek.parser

import io.github.kkarnauk.parsek.token.IndexedTokenProducer

/**
 * Required to parse input and return [ParseResult] of [T].
 */
public sealed interface Parser<out T> {
    /**
     * Tries to parse tokens from [tokenProducer] starting from [fromIndex].
     */
    public fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T>
}

/**
 * Parser that doesn't ignore parsing results. For more information check for combinators.
 */
public interface OrdinaryParser<out T> : Parser<T>

/**
 * Parser that ignores parsing results. For more information check for combinators.
 */
public class SkipParser<out T> internal constructor(internal val inner: OrdinaryParser<T>) : Parser<T> by inner

public fun <T> Parser<T>.parseToEnd(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
    return when (val result = parse(tokenProducer, fromIndex)) {
        is ParseFailure -> result
        is SuccessfulParse -> when (val nextToken = tokenProducer.getOrNull(result.nextTokenIndex)) {
            null -> result
            else -> UnparsedRemainderFailure(nextToken)
        }
    }
}
