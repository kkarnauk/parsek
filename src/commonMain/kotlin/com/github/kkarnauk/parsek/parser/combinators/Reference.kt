package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParseResult
import com.github.kkarnauk.parsek.parser.SkipParser
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

/**
 * @return [OrdinaryParser] from [parserGetter] which is calculated only once when it is used for the first time.
 * Usually required to allow using not initialized parsers.
 */
public fun <T> ref(parserGetter: () -> OrdinaryParser<T>): OrdinaryParser<T> = object : OrdinaryParser<T> {
    private val parser by lazy(parserGetter)

    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
        return parser.parse(tokenProducer, fromIndex)
    }
}

/**
 * @return [SkipParser] from [parserGetter] which is calculated only once when it is used for the first time.
 * Usually required to allow using not initialized parsers.
 */
public fun <T> ref(parserGetter: () -> SkipParser<T>): SkipParser<T> = object : OrdinaryParser<T> {
    private val parser by lazy { parserGetter().inner }

    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T> {
        return parser.parse(tokenProducer, fromIndex)
    }
}.skip()
