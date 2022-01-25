package com.github.kkarnauk.parsek.token.tokenizer

import com.github.kkarnauk.parsek.token.types.CharPredicateTokenType
import com.github.kkarnauk.parsek.token.types.CharTokenType
import com.github.kkarnauk.parsek.token.types.LiteralTokenType
import com.github.kkarnauk.parsek.token.types.RegexTokenType
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE")
internal class LongestMatchTokenizerTest : AbstractTokenizerTest<LongestMatchTokenizer>() {
    override val tokenizerSupplier: TokenizerSupplier<LongestMatchTokenizer>
        get() = longestMatchTokenizerSupplier

    @Test
    fun testTokenizingBits() = doTest {
        text = "1 0 00\n1\n1 0\t 1\n\n  0  1"

        val zero by LiteralTokenType("0", false, tokenTypeName(), false)
        val one by LiteralTokenType("1", false, tokenTypeName(), false)
        val ws by RegexTokenType("\\s+", setOf(), tokenTypeName(), true)
        expectedTokenDescriptions = listOf(
            describe(one, 0, 1),
            describe(zero, 2, 1),
            describe(zero, 4, 1),
            describe(zero, 5, 1),
            describe(one, 7, 1),
            describe(one, 9, 1),
            describe(zero, 11, 1),
            describe(one, 14, 1),
            describe(zero, 19, 1),
            describe(one, 22, 1)
        )
    }

    @Test
    fun testTokenizingDataDescription() = doTest {
        text = "person(name='personImportant' age=21 hobbies='namesCode')  "

        val dataName by LiteralTokenType("person", false, tokenTypeName(), false)
        val nameField by LiteralTokenType("name", false, tokenTypeName(), false)
        val ageField by LiteralTokenType("age", false, tokenTypeName(), false)
        val hobbiesField by LiteralTokenType("hobbies", false, tokenTypeName(), false)
        val str by RegexTokenType("[a-zA-Z]+", setOf(), tokenTypeName(), false)
        val num by RegexTokenType("\\d+", setOf(), tokenTypeName(), false)
        val quote by CharTokenType('\'', false, tokenTypeName(), false)
        val lp by CharTokenType('(', false, tokenTypeName(), false)
        val rp by CharTokenType(')', false, tokenTypeName(), false)
        val eq by CharTokenType('=', false, tokenTypeName(), false)
        val ws by RegexTokenType("\\s+", setOf(), tokenTypeName(), true)
        expectedTokenDescriptions = listOf(
            describe(dataName, 0, 6),
            describe(lp, 6, 1),
            describe(nameField, 7, 4),
            describe(eq, 11, 1),
            describe(quote, 12, 1),
            describe(str, 13, 15),
            describe(quote, 28, 1),
            describe(ageField, 30, 3),
            describe(eq, 33, 1),
            describe(num, 34, 2),
            describe(hobbiesField, 37, 7),
            describe(eq, 44, 1),
            describe(quote, 45, 1),
            describe(str, 46, 9),
            describe(quote, 55, 1),
            describe(rp, 56, 1)
        )
    }

    @Test
    fun testPrefixes() = doTest {
        text = "a \n\naa \naaa"

        val a by LiteralTokenType("a", false, tokenTypeName(), false)
        val aa by LiteralTokenType("aa", false, tokenTypeName(), false)
        val aaa by LiteralTokenType("aaa", false, tokenTypeName(), false)
        val ws by CharPredicateTokenType(tokenTypeName(), true) { it.isWhitespace() }
        expectedTokenDescriptions = listOf(
            describe(a, 0, 1),
            describe(aa, 4, 2),
            describe(aaa, 8, 3)
        )
    }

    @Test
    fun testCannotTokenizeToEnd() = assertThrows<IllegalArgumentException> {
        doTest {
            text = "abc"

            val a by LiteralTokenType("a", false, tokenTypeName(), false)
            val b by LiteralTokenType("b", false, tokenTypeName(), false)
            expectedTokenDescriptions = listOf()
        }
    }
}
