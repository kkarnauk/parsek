package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.ParsekTest
import io.github.kkarnauk.parsek.token.tokenizer.provider.TokenizerProvider
import io.github.kkarnauk.parsek.token.type.TokenType
import kotlin.properties.Delegates
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

internal abstract class AbstractTokenizerTest<T : Tokenizer> : ParsekTest() {
    protected abstract val tokenizerProvider: TokenizerProvider<T>

    protected inner class InsideTest : AbstractInsideTest() {
        private val tokenTypes = mutableListOf<TokenType>()

        override var text by Delegates.notNull<CharSequence>()
        var expectedTokenDescriptions by Delegates.notNull<List<TokenDescription>>()

        fun test() {
            val tokenizer = tokenizerProvider.provide(tokenTypes)
            val tokens = tokenizer.tokenize(text).toList()
            val expectedTokens = expectedTokenDescriptions.map { it.toToken() }
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
