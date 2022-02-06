package io.github.kkarnauk.parsek.parser.combinator

import io.github.kkarnauk.parsek.info.EmptyLocation
import io.github.kkarnauk.parsek.info.Location
import io.github.kkarnauk.parsek.parser.*
import io.github.kkarnauk.parsek.parser.AbstractParserTest
import io.github.kkarnauk.parsek.token.Token
import io.github.kkarnauk.parsek.token.type.CharPredicateTokenType
import io.github.kkarnauk.parsek.token.type.CharTokenType
import io.github.kkarnauk.parsek.token.type.RegexTokenType
import kotlin.test.Test

internal class SeparatedTest : AbstractParserTest<OrdinaryParser<*>>() {
    private val letter = CharPredicateTokenType(tokenTypeName(), false) { it.isLetter() }
    private val space = CharTokenType(' ', false, tokenTypeName(), false)

    private val numT = RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
    private val num = numT map { it.text.toInt() }

    @Test
    fun testSeparatedWithOrdinary() = doTest<List<String>> {
        text = "h o m e"
        parser = separated(letter, space) map { tokens -> tokens.map { it.text } }
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(space, 1, 1),
            describeToken(letter, 2, 1),
            describeToken(space, 3, 1),
            describeToken(letter, 4, 1),
            describeToken(space, 5, 1),
            describeToken(letter, 6, 1)
        )
        expected = ParsedValue(listOf("h", "o", "m", "e"), 7)
    }

    @Test
    fun testSeparatedWithOrdinaryExtraSeparator() = doTest<List<Token>> {
        text = "a b "
        parser = separated(letter, space)
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(space, 1, 1),
            describeToken(letter, 2, 1),
            describeToken(space, 3, 1)
        )
        expected = ParsedValue(
            listOf(describeToken(letter, 0, 1).toToken(), describeToken(letter, 2, 1).toToken()),
            3
        )
    }

    @Test
    fun testSeparatedWithOrdinaryAllowEmpty() = doTest<List<Token>> {
        text = "a"
        parser = separated(letter, space, allowEmpty = true)
        fromIndex = 1
        tokenProducer = produceTokens(describeToken(letter, 0, 1))
        expected = ParsedValue(listOf(), 1)
    }

    @Test
    fun testSeparatedWithOrdinaryEmptyWithoutAllowing() = doTest<Nothing> {
        text = "a"
        parser = separated(letter, space)
        fromIndex = 1
        tokenProducer = produceTokens(describeToken(letter, 0, 1))
        expected = unexpectedEofFailure(EmptyLocation, letter)
    }

    @Test
    fun testSeparatedWithSkip() = doTest<List<String>> {
        text = "a b "
        parser = (separated(-letter, space) map { tokens -> tokens.map { it.text } }).toOrdinary()
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(space, 1, 1),
            describeToken(letter, 2, 1),
            describeToken(space, 3, 1)
        )
        expected = ParsedValue(listOf("a", "b"), 3)
    }

    @Test
    fun testLeftAssociative() = doTest<Int> {
        text = "10 2 32 4 12 6"
        parser = leftAssociative(num, num) { res, x, y -> res + x - y }
        fromIndex = 2
        tokenProducer = produceTokens(
            describeToken(numT, 0, 2),
            describeToken(numT, 3, 1),
            describeToken(numT, 5, 2),
            describeToken(numT, 8, 1),
            describeToken(numT, 10, 2),
            describeToken(numT, 13, 1)
        )
        expected = ParsedValue(32 - 4 + 12, 5)
    }

    @Test
    fun testLeftAssociativeWithoutSeparatorInTransform() = doTest<Int> {
        text = "10 a 20 b 30"
        parser = leftAssociative(num, -letter) { res, current -> res - current }
        tokenProducer = produceTokens(
            describeToken(numT, 0, 2),
            describeToken(letter, 3, 1),
            describeToken(numT, 5, 2),
            describeToken(letter, 8, 1),
            describeToken(numT, 10, 2)
        )
        expected = ParsedValue(10 - 20 - 30, 5)
    }

    @Test
    fun testLeftAssociativeWithoutSeparator() = doTest<String> {
        text = "a b c"
        parser = leftAssociative(letter map { it.text }) { res, current -> res + current }
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(letter, 2, 1),
            describeToken(letter, 4, 1)
        )
        expected = ParsedValue("abc", 3)
    }

    @Test
    fun testLeftAssociativeEmpty() = doTest<Nothing> {
        text = "a"
        parser = leftAssociative(num, space) { res, current -> res + current }
        tokenProducer = produceTokens(describeToken(letter, 0, 1))
        expected = MismatchTokenTypeFailure(Location(0, 1, 1), numT, letter)
    }

    @Test
    fun testRightAssociative() = doTest<Int> {
        text = "10 2 32 4 12 6"
        parser = rightAssociative(num, num) { x, y, res -> res + x - y }
        fromIndex = 2
        tokenProducer = produceTokens(
            describeToken(numT, 0, 2),
            describeToken(numT, 3, 1),
            describeToken(numT, 5, 2),
            describeToken(numT, 8, 1),
            describeToken(numT, 10, 2),
            describeToken(numT, 13, 1)
        )
        expected = ParsedValue(32 - 4 + 12, 5)
    }

    @Test
    fun testRightAssociativeWithoutSeparatorInTransform() = doTest<Int> {
        text = "10 a 20 b 30"
        parser = rightAssociative(num, -letter) { current, res -> res - current }
        tokenProducer = produceTokens(
            describeToken(numT, 0, 2),
            describeToken(letter, 3, 1),
            describeToken(numT, 5, 2),
            describeToken(letter, 8, 1),
            describeToken(numT, 10, 2)
        )
        expected = ParsedValue(30 - 20 - 10, 5)
    }

    @Test
    fun testRightAssociativeWithoutSeparator() = doTest<String> {
        text = "a b c"
        parser = rightAssociative(letter map { it.text }) { current, res -> res + current }
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(letter, 2, 1),
            describeToken(letter, 4, 1)
        )
        expected = ParsedValue("cba", 3)
    }

    @Test
    fun testRightAssociativeEmpty() = doTest<Nothing> {
        text = "a"
        parser = rightAssociative(num, space) { current, res -> res + current }
        tokenProducer = produceTokens(describeToken(letter, 0, 1))
        expected = MismatchTokenTypeFailure(Location(0, 1, 1), numT, letter)
    }
}
