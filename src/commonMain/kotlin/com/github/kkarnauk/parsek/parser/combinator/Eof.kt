package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.exception.ParseException
import com.github.kkarnauk.parsek.parser.MismatchTokenTypeFailure
import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParseResult
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.IndexedTokenProducer
import com.github.kkarnauk.parsek.token.type.EofTokenType

/**
 * @return [OrdinaryParser] that returns [Unit] if `fromIndex` is at least the number of tokens.
 * Otherwise, returns [MismatchTokenTypeFailure].
 */
public fun eofParser(): OrdinaryParser<Unit> = object : OrdinaryParser<Unit> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Unit> {
        if (fromIndex < 0) {
            throw ParseException("Unexpected value of 'fromIndex=$fromIndex'.")
        }
        return when (val token = tokenProducer.getOrNull(fromIndex)) {
            null -> ParsedValue(Unit, fromIndex)
            else -> MismatchTokenTypeFailure(token.location, EofTokenType, token.type)
        }
    }
}
