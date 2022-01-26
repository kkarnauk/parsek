package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.ParsekTest
import com.github.kkarnauk.parsek.token.Token
import com.github.kkarnauk.parsek.token.TokenProducer
import com.github.kkarnauk.parsek.token.indexed
import kotlin.properties.Delegates
import kotlin.test.assertEquals

internal abstract class AbstractParserTest<P : Parser<*>> : ParsekTest() {
    protected abstract val parser: P

    protected fun produceTokens(vararg tokens: TokenDescription): TokenProducer = object : TokenProducer {
        var last = -1

        override fun nextToken(): Token? = tokens.getOrNull(last++)?.toToken()
    }

    protected inner class InsideTest<R> : AbstractInsideTest() {
        override var text by Delegates.notNull<CharSequence>()
        var fromIndex = 0
        var tokenProducer by Delegates.notNull<TokenProducer>()
        var expected by Delegates.notNull<ParseResult<R>>()

        fun test() {
            assertEquals(expected, parser.parse(tokenProducer.indexed(), fromIndex))
        }
    }

    protected fun <T> doTest(block: InsideTest<T>.() -> Unit) {
        InsideTest<T>().apply(block).apply { test() }
    }
}
