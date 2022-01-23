package com.github.kkarnauk.parsek.token.types

import kotlin.test.Test

internal class CharTokenTypeTest : AbstractTokenTypeTest<CharTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = CharTokenType('a', false, name, false)
        text = "a"
        expectedLength = 1
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = CharTokenType('z', false, name, false)
        text = "my zebra"
        fromIndex = 3
        expectedLength = 1
    }

    @Test
    fun testIgnoreCase() = doTest {
        token = CharTokenType('S', true, name, false)
        text = "superman"
        expectedLength = 1
    }

    @Test
    fun testCaseSensitive() = doTest {
        token = CharTokenType('q', false, name, false)
        text = "Quality"
        expectedLength = 0
    }

    @Test
    fun testRussian() = doTest {
        token = CharTokenType('Д', true, name, false)
        text = "Подвал"
        fromIndex = 2
        expectedLength = 1
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = CharTokenType('r', false, name, false)
        text = "russian"
        fromIndex = 3
        expectedLength = 0
    }
}
