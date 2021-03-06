package io.github.kkarnauk.parsek.token.type

import io.github.kkarnauk.parsek.info.EmptyLocation
import io.github.kkarnauk.parsek.parser.*
import io.github.kkarnauk.parsek.token.IndexedTokenProducer
import io.github.kkarnauk.parsek.token.Token

/**
 * Represents different tokens before tokenizing.
 */
public interface TokenType : OrdinaryParser<Token> {
    /**
     * Whether this token type should or shouldn't be passed to a parser.
     */
    public val ignored: Boolean

    /**
     * Name of this token. It should be unique within a parser, but it's not checked.
     */
    public val name: String

    /**
     * @return length of matched part of [input] starting from [fromIndex].
     * If result equals 0, nothing is matched.
     */
    public fun match(input: CharSequence, fromIndex: Int): Int
}

/**
 * Intermediate class to implement all other token types.
 */
public abstract class AbstractTokenType(
    override val name: String,
    override val ignored: Boolean
) : TokenType {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Token> {
        val token = tokenProducer.getOrNull(fromIndex)
        return when {
            token == null -> {
                val location = tokenProducer.lastToken?.location ?: EmptyLocation // TODO add last token to location
                unexpectedEofFailure(location, this)
            }
            token.type === this -> ParsedValue(token, fromIndex + 1)
            else -> MismatchTokenTypeFailure(token.location, this, token.type)
        }
    }

    override fun toString(): String = name
}
