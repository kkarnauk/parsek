package com.github.kkarnauk.parsek.token.types

import kotlin.test.Test

internal class CharTokenTypeTest : AbstractTokenTypeTest<CharTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = CharTokenType('a', false, tokenTypeName(), false)
        text = "a"
        expectedLength = 1
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = CharTokenType('z', false, tokenTypeName(), false)
        text = "my zebra"
        fromIndex = 3
        expectedLength = 1
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = CharTokenType('S', true, tokenTypeName(), false)
        text = "superman"
        expectedLength = 1
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = CharTokenType('q', false, tokenTypeName(), false)
        text = "Quality"
        expectedLength = 0
    }

    @Test
    fun testRussian() = doTest {
        token = CharTokenType('Д', true, tokenTypeName(), false)
        text = "Подвал"
        fromIndex = 2
        expectedLength = 1
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = CharTokenType('r', false, tokenTypeName(), false)
        text = "russian"
        fromIndex = 3
        expectedLength = 0
    }
}
