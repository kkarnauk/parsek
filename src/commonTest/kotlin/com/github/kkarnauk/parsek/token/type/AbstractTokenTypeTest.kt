package com.github.kkarnauk.parsek.token.type

import com.github.kkarnauk.parsek.ParsekTest
import kotlin.properties.Delegates
import kotlin.test.assertEquals

internal abstract class AbstractTokenTypeTest<T : TokenType> : ParsekTest() {
    protected inner class InsideTest : AbstractInsideTest() {
        override var text by Delegates.notNull<CharSequence>()
        var token by Delegates.notNull<T>()
        var fromIndex = 0
        var expectedLength by Delegates.notNull<Int>()

        fun test() {
            assertEquals(expectedLength, token.match(text, fromIndex))
        }
    }

    protected fun doTest(block: InsideTest.() -> Unit) {
        InsideTest().apply(block).apply { test() }
    }
}
