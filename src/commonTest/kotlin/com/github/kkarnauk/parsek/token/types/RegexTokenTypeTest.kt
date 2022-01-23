package com.github.kkarnauk.parsek.token.types

import kotlin.test.Test

internal class RegexTokenTypeTest : AbstractTokenTypeTest<RegexTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = RegexTokenType("\\s+", emptySet(), name, false)
        text = " \t\t \n\n   \n"
        expectedLength = 10
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = RegexTokenType("[aA][bB][aA][cC]*", emptySet(), name, false)
        text = "hi, AbaCcCcDd"
        fromIndex = 4
        expectedLength = 7
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = RegexTokenType("hello my Friend!", setOf(RegexOption.IGNORE_CASE), name, false)
        text = "hello my fRIEND!"
        expectedLength = 16
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = RegexTokenType("My name", emptySet(), name, false)
        text = "my name"
        expectedLength = 0
    }

    @Test
    fun testRussian() = doTest {
        token = RegexTokenType("Привет!", setOf(RegexOption.IGNORE_CASE), name, false)
        text = "Мой друг, Привет! Как дела?"
        fromIndex = 10
        expectedLength = 7
    }

    @Test
    fun testTextFullyMatchedButTokenNot() = doTest {
        token = RegexTokenType("[a]*b", emptySet(), name, false)
        text = "aaaaaaaaaaaaaB"
        expectedLength = 0
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = RegexTokenType("[a][b]", emptySet(), name, false)
        text = "cab"
        expectedLength = 0
    }
}
