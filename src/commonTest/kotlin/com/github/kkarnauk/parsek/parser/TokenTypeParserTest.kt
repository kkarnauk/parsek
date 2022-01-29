package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.types.CharTokenType
import com.github.kkarnauk.parsek.token.types.LiteralTokenType
import com.github.kkarnauk.parsek.token.types.RegexTokenType
import com.github.kkarnauk.parsek.token.types.TokenType
import kotlin.test.Test

internal class TokenTypeParserTest : AbstractParserTest<TokenType>() {
    @Test
    fun testLiteralTokenType() = doTest<Token> {
        text = "Kirill"
        parser = LiteralTokenType("Kirill", false, tokenTypeName(), false)
        tokenProducer = produceTokens(describeToken(parser, 0, 6))
        expected = ParsedValue(describeToken(parser, 0, 6).toToken(), 1)
    }

    @Test
    fun testRegexTokenType() = doTest<Token> {
        text = "one two"
        val word = RegexTokenType("[a-z]+", setOf(), tokenTypeName(), false)
        val ws = RegexTokenType("\\s+", setOf(), tokenTypeName(), false)
        parser = word
        tokenProducer = produceTokens(
            describeToken(word, 0, 3),
            describeToken(ws, 3, 1),
            describeToken(word, 4, 3)
        )
        expected = ParsedValue(describeToken(word, 0, 3).toToken(), 1)
    }

    @Test
    fun testMismatchTokenTypeFailure() = doTest<Nothing> {
        text = "bba"
        val a = CharTokenType('a', false, tokenTypeName(), false)
        val b = CharTokenType('b', false, tokenTypeName(), false)
        parser = b
        fromIndex = 2
        tokenProducer = produceTokens(
            describeToken(b, 0, 1),
            describeToken(b, 1, 1),
            describeToken(a, 2, 1)
        )
        expected = MismatchTokenTypeFailure(Location(2, 1, 3), b, a)
    }
}
