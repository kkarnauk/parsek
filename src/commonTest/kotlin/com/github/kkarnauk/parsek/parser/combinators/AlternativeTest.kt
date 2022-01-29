package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.types.CharTokenType
import com.github.kkarnauk.parsek.token.types.LiteralTokenType
import com.github.kkarnauk.parsek.token.types.RegexTokenType
import kotlin.test.Test

internal class AlternativeTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testPickFirstAlternative() = doTest<Token> {
        text = "12 1234"

        val twoDigitNumber = RegexTokenType("[0-9][0-9]", setOf(), tokenTypeName(), false)
        parser = twoDigitNumber alt (twoDigitNumber seq twoDigitNumber)
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(twoDigitNumber, 0, 2),
            describeToken(twoDigitNumber, 3, 2),
            describeToken(twoDigitNumber, 5, 2)
        )
        expected = ParsedValue(describeToken(twoDigitNumber, 3, 2).toToken(), 2)
    }

    @Test
    fun testManyAlternatives() = doTest<Token> {
        text = "hello my old friend"
        val hello = LiteralTokenType("hello", false, tokenTypeName(), false)
        val my = LiteralTokenType("my", false, tokenTypeName(), false)
        val old = LiteralTokenType("old", false, tokenTypeName(), false)
        val friend = LiteralTokenType("friend", false, tokenTypeName(), false)
        parser = hello alt my alt old alt friend
        fromIndex = 3
        tokenProducer = produceTokens(
            describeToken(hello, 0, 5),
            describeToken(my, 6, 2),
            describeToken(old, 9, 3),
            describeToken(friend, 13, 6)
        )
        expected = ParsedValue(describeToken(friend, 13, 6).toToken(), 4)
    }

    @Test
    fun testSkipInAlternative() = doTest<String> {
        text = "ab"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        parser = ((-a alt -b) seq (-a alt -b)).toOrdinary() map { (x, y) -> x.text + y.text }
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(b, 1, 1)
        )
        expected = ParsedValue("ab", 2)
    }

    @Test
    fun testAlternativeFailure() = doTest<Nothing> {
        text = "02"
        val zero = CharTokenType('0', false, tokenTypeName(), false)
        val one = CharTokenType('1', false, tokenTypeName(), false)
        val two = CharTokenType('2', false, tokenTypeName(), false)
        parser = (zero seq one) alt (one seq zero)
        tokenProducer = produceTokens(
            describeToken(zero, 0, 1),
            describeToken(two, 1, 1)
        )
        expected = NoSuchAlternativeFailure(
            Location(0, 1, 1),
            listOf(
                MismatchTokenTypeFailure(Location(1, 1, 2), one, two),
                MismatchTokenTypeFailure(Location(0, 1, 1), one, zero)
            )
        )
    }
}
