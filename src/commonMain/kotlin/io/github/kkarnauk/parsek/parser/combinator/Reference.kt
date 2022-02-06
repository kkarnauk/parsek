package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.ParseResult
import io.github.kkarnauk.parsek.parser.SkipParser
import io.github.kkarnauk.parsek.token.IndexedTokenProducer

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
