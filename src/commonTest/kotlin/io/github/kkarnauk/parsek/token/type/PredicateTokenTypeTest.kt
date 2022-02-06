package io.github.kkarnauk.parsek.token.type

import kotlin.test.Test

internal class PredicateTokenTypeTest : AbstractTokenTypeTest<PredicateTokenType>() {
    @Test
    fun testMiddleMatch() = doTest {
        token = PredicateTokenType(tokenTypeName(), false) { input, fromIndex ->
            input.length - fromIndex
        }
        text = "he-he-he"
        fromIndex = 3
        expectedLength = 5
    }

    @Test
    fun testWithoutFromIndex() = doTest {
        token = predicateTokenType(tokenTypeName(), false) { it.indexOf('a') }
        text = "Anna, look at you!"
        fromIndex = 5
        expectedLength = 6
    }

    @Test
    fun testIncorrectIndex() = doTest {
        token = PredicateTokenType(tokenTypeName(), false) { input, fromIndex ->
            if (input.startsWith("Never", fromIndex)) input.length - fromIndex else 0
        }
        text = "No! Never mess with me!"
        fromIndex = 1
        expectedLength = 0
    }

    @Test
    fun testComplexCondition() = doTest {
        token = predicateTokenType(tokenTypeName(), false) {
            if (it.startsWith("first", true) && it.endsWith("second", true)) it.length else 0
        }
        text = "Go over here, First and Second"
        fromIndex = 14
        expectedLength = 16
    }
}
