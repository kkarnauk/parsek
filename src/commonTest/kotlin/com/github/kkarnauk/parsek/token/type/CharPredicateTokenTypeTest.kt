package com.github.kkarnauk.parsek.token.type

import kotlin.test.Test

internal class CharPredicateTokenTypeTest : AbstractTokenTypeTest<CharPredicateTokenType>() {
    @Test
    fun testFullMatch() = doTest {
        token = CharPredicateTokenType(tokenTypeName(), false) { it.isLowerCase() }
        text = "rock"
        expectedLength = 4
    }

    @Test
    fun testMiddleMatch() = doTest {
        token = CharPredicateTokenType(tokenTypeName(), false) { it.isLetter() }
        text = "Give me, plEaSe, 10 dollars."
        fromIndex = 9
        expectedLength = 6
    }

    @Test
    fun testPrefixMatch() = doTest {
        token = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
        text = "15 dollars"
        expectedLength = 2
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = CharPredicateTokenType(tokenTypeName(), false) { it.isDigit() }
        text = "15 dollars"
        fromIndex = 3
        expectedLength = 0
    }

    // Cannot inherit function in JS
    fun interface CharToBoolean {
        fun invoke(c: Char): Boolean
    }

    @Test
    fun testComplexCondition() = doTest {
        token = CharPredicateTokenType(tokenTypeName(), false, object : CharToBoolean {
            var last = -1
            override fun invoke(c: Char) = (last < c.code).apply { last = c.code }
        }::invoke)

        text = "bcdefabcde"
        expectedLength = 5
    }
}
