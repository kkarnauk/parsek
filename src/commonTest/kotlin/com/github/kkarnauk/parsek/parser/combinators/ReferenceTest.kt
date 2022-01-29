package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.parser.AbstractParserTest
import com.github.kkarnauk.parsek.token.types.CharPredicateTokenType
import kotlin.test.Test

internal class ReferenceTest : AbstractParserTest<OrdinaryParser<*>>() {
    private val digit = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
    private val num: OrdinaryParser<Int> = (digit seq ref(::num) map { (x, y) ->
        (x.text + y).toInt()
    }) alt (digit map { it.text.toInt() })

    private val letter = CharPredicateTokenType(tokenTypeName(), false) { it.isLetter() }
    private val word: SkipParser<String> = (letter.skip() seq ref(::word) map { (x, y) ->
        x.text + y
    }) alt (letter map { it.text }).skip()

    @Test
    fun testReferenceInOrdinary() = doTest<Int> {
        text = "1734"
        parser = num
        tokenProducer = produceTokens(
            describeToken(digit, 0, 1),
            describeToken(digit, 1, 1),
            describeToken(digit, 2, 1),
            describeToken(digit, 3, 1)
        )
        expected = ParsedValue(1734, 4)
    }

    @Test
    fun testReferenceInSkip() = doTest<String> {
        text = "home"
        parser = +word
        tokenProducer = produceTokens(
            describeToken(letter, 0, 1),
            describeToken(letter, 1, 1),
            describeToken(letter, 2, 1),
            describeToken(letter, 3, 1)
        )
        expected = ParsedValue("home", 4)
    }

    @Test
    fun testFailureInOrdinaryReference() = doTest<Nothing> {
        text = "a"
        parser = num
        tokenProducer = produceTokens(describeToken(letter, 0, 1))
        expected = NoSuchAlternativeFailure(
            Location(0, 1, 1),
            listOf(
                MismatchTokenTypeFailure(Location(0, 1, 1), digit, letter),
                MismatchTokenTypeFailure(Location(0, 1, 1), digit, letter)
            )
        )
    }

    @Test
    fun testFailureInSkipReference() = doTest<Nothing> {
        text = "1"
        parser = +word
        tokenProducer = produceTokens(describeToken(digit, 0, 1))
        expected = NoSuchAlternativeFailure(
            Location(0, 1, 1),
            listOf(
                MismatchTokenTypeFailure(Location(0, 1, 1), letter, digit),
                MismatchTokenTypeFailure(Location(0, 1, 1), letter, digit)
            )
        )
    }
}
