package com.github.kkarnauk.parsek.token.types

import kotlin.properties.Delegates
import kotlin.test.assertEquals

internal abstract class AbstractTokenTypeTest<T : TokenType> {
    protected inner class InsideTest {
        var token by Delegates.notNull<T>()
        var text by Delegates.notNull<CharSequence>()
        var fromIndex = 0
        var expectedLength by Delegates.notNull<Int>()

        fun test() {
            assertEquals(expectedLength, token.match(text, fromIndex))
        }
    }

    protected fun doTest(block: InsideTest.() -> Unit) {
        InsideTest().apply(block).apply { test() }
    }

    protected val name = "Some name"
}
