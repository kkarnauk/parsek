package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.parser.AbstractParserTest
import com.github.kkarnauk.parsek.parser.MismatchTokenTypeFailure
import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.ParsedValue
import com.github.kkarnauk.parsek.token.types.CharPredicateTokenType
import com.github.kkarnauk.parsek.token.types.RegexTokenType
import kotlin.test.Test

internal class MapTest : AbstractParserTest<OrdinaryParser<*>>() {
    @Test
    fun testMapInOrdinary() = doTest<Int> {
        text = "2431"
        val num = RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
        parser = num map { it.text.toInt() }
        tokenProducer = produceTokens(describeToken(num, 0, 4))
        expected = ParsedValue(2431, 1)
    }

    @Test
    fun testMapInSkip() = doTest<Int> {
        text = "1 1234"
        val num = RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
        parser = +(num.skip() map { it.text.toInt() })
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(num, 0, 1),
            describeToken(num, 2, 4)
        )
        expected = ParsedValue(1234, 2)
    }

    @Test
    fun testMapInSeparated() = doTest<Int> {
        text = "1a2b3c4"
        val num = RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
        val word = RegexTokenType("[a-z]+", setOf(), tokenTypeName(), false)
        parser = separated(num, word) map { tokens -> tokens.sumOf { it.text.toInt() } }
        tokenProducer = produceTokens(
            describeToken(num, 0, 1),
            describeToken(word, 1, 1),
            describeToken(num, 2, 1),
            describeToken(word, 3, 1),
            describeToken(num, 4, 1),
            describeToken(word, 5, 1),
            describeToken(num, 6, 1)
        )
        expected = ParsedValue(10, 7)
    }

    @Test
    fun testMismatchTokenTypeFailureInMap() = doTest<Nothing> {
        text = "a 12"
        val word = CharPredicateTokenType(tokenTypeName(), false) { it.isLetter() }
        val num = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
        parser = word map { it.text }
        fromIndex = 1
        tokenProducer = produceTokens(
            describeToken(word, 0, 1),
            describeToken(num, 2, 2)
        )
        expected = MismatchTokenTypeFailure(Location(2, 1, 3), word, num)
    }
}
