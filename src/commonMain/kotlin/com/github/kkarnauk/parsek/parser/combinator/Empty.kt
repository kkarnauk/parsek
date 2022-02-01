package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParseResult
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private object EmptyCombinator : OrdinaryParser<Unit> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Unit> {
        return ParsedValue(Unit, fromIndex)
    }
}

/**
 * @return [OrdinaryParser] that doesn't consume any tokens and always returns [ParsedValue] with [Unit].
 */
public fun emptyParser(): OrdinaryParser<Unit> = EmptyCombinator
