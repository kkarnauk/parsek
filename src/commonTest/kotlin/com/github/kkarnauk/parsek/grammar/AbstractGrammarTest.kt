package com.github.kkarnauk.parsek.grammar

import com.github.kkarnauk.parsek.ParsekTest
import kotlin.properties.Delegates
import kotlin.test.assertEquals

internal abstract class AbstractGrammarTest<R : Any, T : Grammar<R>> : ParsekTest() {
    abstract val grammar: T

    protected inner class InsideTest : AbstractInsideTest() {
        override var text by Delegates.notNull<CharSequence>()
        var expected by Delegates.notNull<R>()

        fun test() {
            val actual = grammar.parse(text) // evaluating before in order not to call [expected] until actually needed
            assertEquals(expected, actual)
        }
    }

    protected fun doTest(block: InsideTest.() -> Unit) {
        InsideTest().apply(block).apply { test() }
    }

    protected inline fun <reified T : Throwable> doTestThrows(block: InsideTest.() -> Unit) {
        assertThrows<T> {
            InsideTest().apply(block).test()
        }
    }
}
