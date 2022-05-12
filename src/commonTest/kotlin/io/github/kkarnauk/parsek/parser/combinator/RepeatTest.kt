package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.exception.ParserInitializationException
import io.github.kkarnauk.parsek.info.Location
import io.github.kkarnauk.parsek.parser.AbstractParserTest
import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.ParsedValue
import io.github.kkarnauk.parsek.parser.unexpectedEofFailure
import io.github.kkarnauk.parsek.token.type.CharPredicateTokenType
import io.github.kkarnauk.parsek.token.type.CharTokenType
import kotlin.test.Test

internal class RepeatTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testZeroOrMoreOrdinary() = doTest<String> {
        text = "aaaa"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        parser = (a map { it.text }).zeroOrMore() map { it.joinToString("") }
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(a, 1, 1),
            describeToken(a, 2, 1),
            describeToken(a, 3, 1)
        )
        expected = ParsedValue("aaaa", 4)
    }

    @Test
    fun testZeroOrMoreOrdinaryZero() = doTest<String> {
        text = "ba"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        parser = (a map { it.text }).zeroOrMore() map { it.joinToString("") }
        tokenProducer = produceTokens(
            describeToken(b, 0, 1),
            describeToken(a, 1, 1)
        )
        expected = ParsedValue("", 0)
    }

    @Test
    fun testZeroOrMoreSkip() = doTest<Int> {
        text = "123"
        val digit = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
        parser = (-digit map { it.text }).zeroOrMore().toOrdinary() map { it.joinToString("").toInt() }
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(digit, 0, 1),
            describeToken(digit, 1, 1),
            describeToken(digit, 2, 1)
        )
        expected = ParsedValue(23, 3)
    }

    @Test
    fun testOneOrMore() = doTest<List<String>> {
        text = "ab12ab1"
        val digit = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
        val letter = CharTokenType('a', false, tokenTypeName(), false)
        parser = letter.skip().oneOrMore() seq (digit map { it.text }).oneOrMore() seq letter.oneOrMore().skip()
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(letter, 1, 1),
            describeToken(digit, 2, 1),
            describeToken(digit, 3, 1),
            describeToken(letter, 4, 1),
            describeToken(letter, 5, 1),
            describeToken(digit, 6, 1)
        )
        expected = ParsedValue(listOf("1", "2"), 6)
    }

    @Test
    fun testMany() = doTest<String> {
        text = "aaaa"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        parser = (a map { it.text }).many(atLeast = 2, atMost = 3) map { it.joinToString("") }
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(a, 1, 1),
            describeToken(a, 2, 1),
            describeToken(a, 3, 1)
        )
        expected = ParsedValue("aaa", 3)
    }

    @Test
    fun testNotEnoughForAtLeast() = doTest<Nothing> {
        text = "aa"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        parser = a.many(3)
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(a, 1, 1)
        )
        expected = unexpectedEofFailure(Location(1, 1, 2), a)
    }

    @Test
    fun testNegativeAtLeast() = doTestThrows<ParserInitializationException> {
        text = "a"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        parser = a.many(-1)
        tokenProducer = produceTokens(describeToken(a, 0, 1))
    }

    @Test
    fun testAtLeastGreaterAtMost() = doTestThrows<ParserInitializationException> {
        text = "a"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        parser = a.many(2, 1)
        tokenProducer = produceTokens(describeToken(a, 0, 1))
    }
}
