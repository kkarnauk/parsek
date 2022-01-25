package com.github.kkarnauk.parsek

import kotlin.test.fail

internal abstract class ParsekTest {
    protected inline fun <reified T : Throwable> assertThrows(block: () -> Unit) {
        var isThrown = true
        try {
            block()
            isThrown = false
        } catch (t: Throwable) {
            if (t::class != T::class) {
                fail("Expected ${T::class} to be thrown, but ${t::class} was thrown.")
            }
        }
        if (!isThrown) {
            fail("Expected ${T::class} to be thrown, but nothing was thrown.")
        }
    }

    protected val tokenTypeName: () -> String = run {
        var number = 0
        { "Token ${number++}" }
    }
}
