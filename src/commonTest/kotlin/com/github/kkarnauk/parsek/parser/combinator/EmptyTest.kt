package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.parser.AbstractParserTest
import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.type.CharPredicateTokenType
import kotlin.test.Test

internal class EmptyTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testEmptyString() = doTest<Unit> {
        text = ""
        parser = emptyParser()
        tokenProducer = produceTokens()
        expected = ParsedValue(Unit, 0)
    }

    @Test
    fun testNotEmptyString() = doTest<Unit> {
        text = "hey"
        parser = emptyParser()
        val letter = CharPredicateTokenType(tokenTypeName(), false) { it.isLetter() }
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(letter, 1, 1),
            describeToken(letter, 2, 1)
        )
        fromIndex = 1
        expected = ParsedValue(Unit, 1)
    }
}
