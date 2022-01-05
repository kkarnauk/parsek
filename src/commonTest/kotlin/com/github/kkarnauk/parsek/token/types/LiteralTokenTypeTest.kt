package com.github.kkarnauk.parsek.token.types

import kotlin.test.Test

internal class LiteralTokenTypeTest : AbstractTokenTypeTest<LiteralTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = LiteralTokenType("mine", false, name, false)
        text = "mine"
        expectedLength = 4
    }

    @Test
    fun testPrefixMatch() = doTest {
        token = LiteralTokenType("hello", false, name, false)
        text = "hello, my dear friend!"
        expectedLength = 5
    }

    @Test
    fun testSuffixMatch() = doTest {
        token = LiteralTokenType("Friend", false, name, false)
        text = "hello, my dear Friend"
        fromIndex = 15
        expectedLength = 6
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = LiteralTokenType("dear", false, name, false)
        text = "hello, my dear friend!"
        fromIndex = 10
        expectedLength = 4
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = LiteralTokenType("how", false, name, false)
        text = "How are you?"
        expectedLength = 0
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = LiteralTokenType("how", true, name, false)
        text = "How are you?"
        expectedLength = 3
    }

    @Test
    fun testNameNoImpact() = doTest {
        token = LiteralTokenType("hey", false, "other name", false)
        text = "hey!"
        expectedLength = 3
    }

    @Test
    fun testIgnoredNoImpact() = doTest {
        token = LiteralTokenType("hey", false, name, true)
        text = "hey!"
        expectedLength = 3
    }

    @Test
    fun testRussian() = doTest {
        token = LiteralTokenType("Привет", false, name, false)
        text = "Привет, мир!"
        expectedLength = 6
    }

    @Test
    fun testTextFullyMatchedButTokenNot() = doTest {
        token = LiteralTokenType("My lovely friend", false, name, false)
        text = "My love"
        expectedLength = 0
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = LiteralTokenType("Hi", false, name, false)
        text = "Hi, my friend!"
        fromIndex = 2
        expectedLength = 0
    }

    @Test
    fun testIncorrectToken() = doTest {
        token = LiteralTokenType("one", false, name, false)
        text = "two"
        expectedLength = 0
    }
}
