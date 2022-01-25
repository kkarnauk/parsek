package com.github.kkarnauk.parsek.token.types

import kotlin.test.Test

internal class LiteralTokenTypeTest : AbstractTokenTypeTest<LiteralTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = LiteralTokenType("mine", false, tokenTypeName(), false)
        text = "mine"
        expectedLength = 4
    }

    @Test
    fun testPrefixMatch() = doTest {
        token = LiteralTokenType("hello", false, tokenTypeName(), false)
        text = "hello, my dear friend!"
        expectedLength = 5
    }

    @Test
    fun testSuffixMatch() = doTest {
        token = LiteralTokenType("Friend", false, tokenTypeName(), false)
        text = "hello, my dear Friend"
        fromIndex = 15
        expectedLength = 6
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = LiteralTokenType("dear", false, tokenTypeName(), false)
        text = "hello, my dear friend!"
        fromIndex = 10
        expectedLength = 4
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = LiteralTokenType("how", false, tokenTypeName(), false)
        text = "How are you?"
        expectedLength = 0
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = LiteralTokenType("how", true, tokenTypeName(), false)
        text = "How are you?"
        expectedLength = 3
    }

    @Test
    fun testIgnoredNoImpact() = doTest {
        token = LiteralTokenType("hey", false, tokenTypeName(), true)
        text = "hey!"
        expectedLength = 3
    }

    @Test
    fun testRussian() = doTest {
        token = LiteralTokenType("Привет", false, tokenTypeName(), false)
        text = "Привет, мир!"
        expectedLength = 6
    }

    @Test
    fun testTextFullyMatchedButTokenNot() = doTest {
        token = LiteralTokenType("My lovely friend", false, tokenTypeName(), false)
        text = "My love"
        expectedLength = 0
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = LiteralTokenType("Hi", false, tokenTypeName(), false)
        text = "Hi, my friend!"
        fromIndex = 2
        expectedLength = 0
    }

    @Test
    fun testIncorrectToken() = doTest {
        token = LiteralTokenType("one", false, tokenTypeName(), false)
        text = "two"
        expectedLength = 0
    }
}
