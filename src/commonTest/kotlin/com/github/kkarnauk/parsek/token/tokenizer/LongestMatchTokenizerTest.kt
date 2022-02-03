package com.github.kkarnauk.parsek.token.tokenizer

import com.github.kkarnauk.parsek.exception.TokenizeException
import com.github.kkarnauk.parsek.token.type.CharPredicateTokenType
import com.github.kkarnauk.parsek.token.type.CharTokenType
import com.github.kkarnauk.parsek.token.type.TextTokenType
import com.github.kkarnauk.parsek.token.type.RegexTokenType
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE")
internal class LongestMatchTokenizerTest : AbstractTokenizerTest<LongestMatchTokenizer>() {
    override val tokenizerProvider: TokenizerProvider<LongestMatchTokenizer>
        get() = longestMatchTokenizerProvider

    @Test
    fun testTokenizingBits() = doTest {
        text = "1 0 00\n1\n1 0\t 1\n\n  0  1"

        val zero by TextTokenType("0", false, tokenTypeName(), false)
        val one by TextTokenType("1", false, tokenTypeName(), false)
        val ws by RegexTokenType("\\s+", setOf(), tokenTypeName(), true)
        expectedTokenDescriptions = listOf(
            describeToken(one, 0, 1),
            describeToken(zero, 2, 1),
            describeToken(zero, 4, 1),
            describeToken(zero, 5, 1),
            describeToken(one, 7, 1),
            describeToken(one, 9, 1),
            describeToken(zero, 11, 1),
            describeToken(one, 14, 1),
            describeToken(zero, 19, 1),
            describeToken(one, 22, 1)
        )
    }

    @Test
    fun testTokenizingDataDescription() = doTest {
        text = "person(name='personImportant' age=21 hobbies='namesCode')  "

        val dataName by TextTokenType("person", false, tokenTypeName(), false)
        val nameField by TextTokenType("name", false, tokenTypeName(), false)
        val ageField by TextTokenType("age", false, tokenTypeName(), false)
        val hobbiesField by TextTokenType("hobbies", false, tokenTypeName(), false)
        val str by RegexTokenType("[a-zA-Z]+", setOf(), tokenTypeName(), false)
        val num by RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
        val quote by CharTokenType('\'', false, tokenTypeName(), false)
        val lp by CharTokenType('(', false, tokenTypeName(), false)
        val rp by CharTokenType(')', false, tokenTypeName(), false)
        val eq by CharTokenType('=', false, tokenTypeName(), false)
        val ws by RegexTokenType("\\s+", setOf(), tokenTypeName(), true)
        expectedTokenDescriptions = listOf(
            describeToken(dataName, 0, 6),
            describeToken(lp, 6, 1),
            describeToken(nameField, 7, 4),
            describeToken(eq, 11, 1),
            describeToken(quote, 12, 1),
            describeToken(str, 13, 15),
            describeToken(quote, 28, 1),
            describeToken(ageField, 30, 3),
            describeToken(eq, 33, 1),
            describeToken(num, 34, 2),
            describeToken(hobbiesField, 37, 7),
            describeToken(eq, 44, 1),
            describeToken(quote, 45, 1),
            describeToken(str, 46, 9),
            describeToken(quote, 55, 1),
            describeToken(rp, 56, 1)
        )
    }

    @Test
    fun testPrefixes() = doTest {
        text = "a \n\naa \naaa"

        val a by TextTokenType("a", false, tokenTypeName(), false)
        val aa by TextTokenType("aa", false, tokenTypeName(), false)
        val aaa by TextTokenType("aaa", false, tokenTypeName(), false)
        val ws by CharPredicateTokenType(tokenTypeName(), true) { it.isWhitespace() }
        expectedTokenDescriptions = listOf(
            describeToken(a, 0, 1),
            describeToken(aa, 4, 2),
            describeToken(aaa, 8, 3)
        )
    }

    @Test
    fun testCannotTokenizeToEnd() = assertThrows<TokenizeException> {
        doTest {
            text = "abc"

            val a by TextTokenType("a", false, tokenTypeName(), false)
            val b by TextTokenType("b", false, tokenTypeName(), false)
            expectedTokenDescriptions = listOf()
        }
    }
}
