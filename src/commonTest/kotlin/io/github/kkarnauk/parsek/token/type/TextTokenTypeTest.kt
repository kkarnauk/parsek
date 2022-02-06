package io.github.kkarnauk.parsek.token.type

import kotlin.test.Test

internal class TextTokenTypeTest : AbstractTokenTypeTest<TextTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = TextTokenType("mine", false, tokenTypeName(), false)
        text = "mine"
        expectedLength = 4
    }

    @Test
    fun testPrefixMatch() = doTest {
        token = TextTokenType("hello", false, tokenTypeName(), false)
        text = "hello, my dear friend!"
        expectedLength = 5
    }

    @Test
    fun testSuffixMatch() = doTest {
        token = TextTokenType("Friend", false, tokenTypeName(), false)
        text = "hello, my dear Friend"
        fromIndex = 15
        expectedLength = 6
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = TextTokenType("dear", false, tokenTypeName(), false)
        text = "hello, my dear friend!"
        fromIndex = 10
        expectedLength = 4
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = TextTokenType("how", false, tokenTypeName(), false)
        text = "How are you?"
        expectedLength = 0
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = TextTokenType("how", true, tokenTypeName(), false)
        text = "How are you?"
        expectedLength = 3
    }

    @Test
    fun testIgnoredNoImpact() = doTest {
        token = TextTokenType("hey", false, tokenTypeName(), true)
        text = "hey!"
        expectedLength = 3
    }

    @Test
    fun testRussian() = doTest {
        token = TextTokenType("Привет", false, tokenTypeName(), false)
        text = "Привет, мир!"
        expectedLength = 6
    }

    @Test
    fun testTextFullyMatchedButTokenNot() = doTest {
        token = TextTokenType("My lovely friend", false, tokenTypeName(), false)
        text = "My love"
        expectedLength = 0
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = TextTokenType("Hi", false, tokenTypeName(), false)
        text = "Hi, my friend!"
        fromIndex = 2
        expectedLength = 0
    }

    @Test
    fun testIncorrectToken() = doTest {
        token = TextTokenType("one", false, tokenTypeName(), false)
        text = "two"
        expectedLength = 0
    }
}
