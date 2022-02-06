package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.exception.ParseException
import io.github.kkarnauk.parsek.parser.MismatchTokenTypeFailure
import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.ParseResult
import io.github.kkarnauk.parsek.parser.ParsedValue
import io.github.kkarnauk.parsek.token.IndexedTokenProducer
import io.github.kkarnauk.parsek.token.type.EofTokenType

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
