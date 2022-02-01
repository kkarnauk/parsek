package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.parser.AbstractParserTest
import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.type.CharTokenType
import kotlin.test.Test

internal class OptionalTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testOrdinaryOptional() = doTest<String?> {
        text = "a b c"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        val c = CharTokenType('c', false, tokenTypeName(), false)
        parser = (a seq b seq a map { (_, x) -> x.text }).optional()
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(b, 2, 1),
            describeToken(c, 3, 1)
        )
        expected = ParsedValue(null, 0)
    }

    @Test
    fun testSkipOptional() = doTest<String?> {
        text = "a b c"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        val c = CharTokenType('c', false, tokenTypeName(), false)
        parser = (-a seq -b seq -a map { (_, x) -> x.text }).optional().toOrdinary()
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(b, 2, 1),
            describeToken(c, 3, 1)
        )
        expected = ParsedValue(null, 0)
    }
}
