package io.github.kkarnauk.parsek.grammar

import io.github.kkarnauk.parsek.exception.toException
import io.github.kkarnauk.parsek.parser.*
import io.github.kkarnauk.parsek.token.indexed
import io.github.kkarnauk.parsek.token.tokenizer.Tokenizer
import io.github.kkarnauk.parsek.token.tokenizer.provider.TokenizerProvider
import io.github.kkarnauk.parsek.token.tokenizer.provider.longestMatchTokenizerProvider
import io.github.kkarnauk.parsek.token.type.TokenType
import io.github.kkarnauk.parsek.token.type.provider.TokenTypeProvider
import kotlin.reflect.KProperty

/**
 * Represents a grammar which is responsible for transforming an input into [R] using [parse]-method.
 *
 * Parses an input tokenizing it at the same time (eats tokens in a lazy way). See [tokenizer] and [parser].
 *
 * Important: you must use delegated properties ([TokenType.getValue], [Grammar.getValue], [Parser.getValue])
 * in order to create tokens or use other grammars.
 * If you create a token type without delegating, you must manually put it into [tokenTypes],
 * otherwise this token will not be used in [tokenizer].
 */
public abstract class Grammar<out R> {
    /**
     * List of tokens that will be used in [tokenizer].
     */
    protected val tokenTypes: MutableList<TokenType> = mutableListOf()

    /**
     * Provides [tokenizer] by substituting [tokenTypes] into [TokenTypeProvider.provide].
     *
     * The default implementation is [longestMatchTokenizerProvider].
     */
    protected open val tokenizerProvider: TokenizerProvider<*>
        get() = longestMatchTokenizerProvider

    /**
     * By default, tokenizing goes at the same time as parsing. Tokens are being eaten in a lazy way.
     * The default implementation uses the longest match tokenizer.
     *
     * If you want to override this, see [tokenizerProvider].
     */
    public val tokenizer: Tokenizer by lazy { tokenizerProvider.provide(tokenTypes) }

    /**
     * The main parser that used in [parse].
     * Note that parsing goes at the same time as tokenizing, by default.
     */
    public abstract val parser: OrdinaryParser<R>

    /**
     * The main method that takes [input], tokenizes and parses it into [R].
     * @see [parser]
     * @see [tokenizer]
     */
    public fun parse(input: CharSequence): R {
        val tokenProducer = tokenizer.tokenize(input).indexed()
        return when (val result = parser.parseToEnd(tokenProducer, 0)) {
            is SuccessfulParse -> result.value
            is ParseFailure -> throw result.toException()
        }
    }

    /**
     * Takes [T] and substitute the name of [kProperty] into [TokenTypeProvider.provide].
     * Puts the provided token type into [tokenTypes] (which is very important).
     */
    protected operator fun <T : TokenType> TokenTypeProvider<T>.provideDelegate(
        thisRef: Grammar<*>,
        kProperty: KProperty<*>
    ): T = provide(kProperty.name).provideDelegate(thisRef, kProperty)

    /**
     * Returns [this] and puts it into [tokenTypes] (which is very important).
     */
    protected operator fun <T : TokenType> T.provideDelegate(
        thisRef: Grammar<*>,
        kProperty: KProperty<*>
    ): T = apply { tokenTypes += this }

    /**
     * Takes all the token types from [G] and puts them all into the current [tokenTypes] (which is very important).
     */
    protected operator fun <G : Grammar<*>> G.provideDelegate(
        thisRef: Grammar<*>,
        kProperty: KProperty<*>
    ): G = apply {
        this@Grammar.tokenTypes += this@provideDelegate.tokenTypes
    }

    /**
     * You really should use this to create new token types in order to be sure that all your token types
     * are used in [tokenizer]. Otherwise, you must put them manually into [tokenTypes].
     */
    protected operator fun <T : TokenType> T.getValue(thisRef: Grammar<*>, kProperty: KProperty<*>): T = this

    /**
     * You don't have to use this, it's just for symmetric usage. But maybe it will be important in the future.
     */
    protected operator fun <P : Parser<*>> P.getValue(thisRef: Grammar<*>, kProperty: KProperty<*>): P = this

    /**
     * You really should use this to bring other grammars in order to be sure that all the token types from [G]
     * are used in [tokenizer]. Otherwise, you must put them manually into [tokenTypes].
     */
    protected operator fun <G : Grammar<*>> G.getValue(thisRef: Grammar<*>, kProperty: KProperty<*>): G = this
}
