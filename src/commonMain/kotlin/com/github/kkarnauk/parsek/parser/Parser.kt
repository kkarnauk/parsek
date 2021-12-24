package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.token.IndexedTokenProducer

/**
 * Required to parse input and return [ParseResult] of [T].
 */
public interface Parser<out T> {
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
public interface SkipParser<out T> : Parser<T>
