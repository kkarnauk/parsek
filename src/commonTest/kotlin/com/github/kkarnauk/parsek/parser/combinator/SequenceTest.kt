package com.github.kkarnauk.parsek.parser.combinator

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.parser.AbstractParserTest
import com.github.kkarnauk.parsek.parser.MismatchTokenTypeFailure
import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.type.CharTokenType
import com.github.kkarnauk.parsek.token.type.TextTokenType
import kotlin.test.Test

internal class SequenceTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testThreeInSequence() = doTest<String> {
        text = "abc"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        val c = CharTokenType('c', false, tokenTypeName(), false)
        parser = a seq b seq c map { (f, s) ->
            s.text + f.second.text + f.first.text
        }
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(b, 1, 1),
            describeToken(c, 2, 1)
        )
        expected = ParsedValue("cba", 3)
    }

    @Test
    fun testSkipOrdinary() = doTest<Token> {
        text = "first second first"
        val first = TextTokenType("first", false, tokenTypeName(), false)
        val second = TextTokenType("second", false, tokenTypeName(), false)
        parser = -first seq second
        tokenProducer = produceTokens(
            describeToken(first, 0, 5),
            describeToken(second, 6, 6),
            describeToken(first, 13, 5)
        )
        expected = ParsedValue(describeToken(second, 6, 6).toToken(), 2)
    }

    @Test
    fun testOrdinarySkip() = doTest<Token> {
        text = "first second first"
        val first = TextTokenType("first", false, tokenTypeName(), false)
        val second = TextTokenType("second", false, tokenTypeName(), false)
        parser = first seq -second
        tokenProducer = produceTokens(
            describeToken(first, 0, 5),
            describeToken(second, 6, 6),
            describeToken(first, 13, 5)
        )
        expected = ParsedValue(describeToken(first, 0, 5).toToken(), 2)
    }

    @Test
    fun testSkipSkip() = doTest<Token> {
        text = "first second first"
        val first = TextTokenType("first", false, tokenTypeName(), false)
        val second = TextTokenType("second", false, tokenTypeName(), false)
        parser = -first seq -second seq first
        tokenProducer = produceTokens(
            describeToken(first, 0, 5),
            describeToken(second, 6, 6),
            describeToken(first, 13, 5)
        )
        expected = ParsedValue(describeToken(first, 13, 5).toToken(), 3)
    }

    @Test
    fun testSkipsAndBack() = doTest<String> {
        text = "a abccba"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        val c = CharTokenType('c', false, tokenTypeName(), false)
        parser = (+(a.skip() seq -b) seq (-c).toOrdinary() seq c.toOrdinary()).skip() seq +-+-b seq a map { (x, y) ->
            x.text + y.text
        }
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(a, 0, 1),
            describeToken(a, 2, 1),
            describeToken(b, 3, 1),
            describeToken(c, 4, 1),
            describeToken(c, 5, 1),
            describeToken(b, 6, 1),
            describeToken(a, 7, 1)
        )
        expected = ParsedValue("ba", 7)
    }

    @Test
    fun testMismatchTokenTypeFailure() = doTest<Nothing> {
        text = "one two\n three"
        val one = TextTokenType("one", false, tokenTypeName(), false)
        val two = TextTokenType("two", false, tokenTypeName(), false)
        val three = TextTokenType("three", false, tokenTypeName(), false)
        parser = one seq two seq one
        tokenProducer = produceTokens(
            describeToken(one, 0, 3),
            describeToken(two, 4, 3),
            describeToken(three, 9, 5)
        )
        expected = MismatchTokenTypeFailure(Location(9, 2, 2), one, three)
    }
}
