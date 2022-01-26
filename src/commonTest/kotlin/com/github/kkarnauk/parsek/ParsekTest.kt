package com.github.kkarnauk.parsek

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.types.TokenType
import kotlin.test.fail

internal abstract class ParsekTest {
    protected val tokenTypeName: () -> String = run {
        var number = 0
        { "Token ${number++}" }
    }

    protected data class TokenDescription(
        val input: CharSequence,
        val tokenType: TokenType,
        val offset: Int,
        val length: Int
    ) {
        private fun locationByOffset(offset: Int): Location {
            require(offset < input.length)
            val beforeOffset = input.substring(0, offset)
            val lastLine = beforeOffset.substringAfterLast('\n', beforeOffset)
            return Location(offset, beforeOffset.count { it == '\n' } + 1, lastLine.count() + 1)
        }

        fun toToken(): Token = Token(tokenType, input, length, locationByOffset(offset))
    }

    protected fun describeToken(
        input: CharSequence,
        tokenType: TokenType,
        offset: Int,
        length: Int
    ): TokenDescription = TokenDescription(input, tokenType, offset, length)

    protected abstract class AbstractInsideTest {
        abstract var text: CharSequence

        fun describeToken(tokenType: TokenType, offset: Int, length: Int): TokenDescription {
            return TokenDescription(text, tokenType, offset, length)
        }
    }

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
}