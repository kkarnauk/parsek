package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.ParseResult
import io.github.kkarnauk.parsek.parser.ParsedValue
import io.github.kkarnauk.parsek.token.IndexedTokenProducer

private object EmptyCombinator : OrdinaryParser<Unit> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Unit> {
        return ParsedValue(Unit, fromIndex)
    }
}

/**
 * @return [OrdinaryParser] that doesn't consume any tokens and always returns [ParsedValue] with [Unit].
 */
public fun emptyParser(): OrdinaryParser<Unit> = EmptyCombinator
