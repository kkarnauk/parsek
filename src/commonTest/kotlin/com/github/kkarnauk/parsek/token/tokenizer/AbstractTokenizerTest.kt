package com.github.kkarnauk.parsek.token.tokenizer

import com.github.kkarnauk.parsek.ParsekTest
import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.types.TokenType
import kotlin.properties.Delegates
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

internal abstract class AbstractTokenizerTest<T : Tokenizer> : ParsekTest() {
    protected abstract val tokenizerSupplier: TokenizerSupplier<T>

    protected data class TokenDescription(val tokenType: TokenType, val offset: Int, val length: Int)

    protected fun describe(tokenType: TokenType, offset: Int, length: Int): TokenDescription {
        return TokenDescription(tokenType, offset, length)
    }

    protected inner class InsideTest {
        private val tokenTypes = mutableListOf<TokenType>()

        var text by Delegates.notNull<String>()
        var expectedTokenDescriptions by Delegates.notNull<List<TokenDescription>>()

        private fun locationByOffset(offset: Int): Location {
            require(offset < text.length)
            val beforeOffset = text.substring(0, offset)
            val lastLine = beforeOffset.substringAfterLast('\n', beforeOffset)
            return Location(offset, beforeOffset.count { it == '\n' } + 1, lastLine.count() + 1)
        }

        fun test() {
            val tokenizer = tokenizerSupplier.supply(tokenTypes)
            val tokens = tokenizer.tokenize(text).toList()
            val expectedTokens = expectedTokenDescriptions.map { (tokenType, offset, length) ->
                Token(tokenType, text, length, locationByOffset(offset))
            }
            assertEquals(expectedTokens, tokens)
        }

        operator fun TokenType.provideDelegate(thisRef: Any?, property: KProperty<*>): TokenType = apply {
            tokenTypes += this
        }

        operator fun TokenType.getValue(thisRef: Any?, property: KProperty<*>): TokenType = this
    }

    protected fun doTest(block: InsideTest.() -> Unit) {
        InsideTest().apply(block).apply { test() }
    }
}
