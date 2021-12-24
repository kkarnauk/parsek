package com.github.kkarnauk.parsek.token.types

import com.github.kkarnauk.parsek.info.EmptyLocation
import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer
import com.github.kkarnauk.parsek.token.Token

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

public abstract class AbstractTokenType(
    override val name: String,
    override val ignored: Boolean
) : TokenType {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<Token> {
        val token = tokenProducer.getOrNull(fromIndex)
        return when {
            token == null -> {
                val location = tokenProducer.lastToken?.location ?: EmptyLocation
                unexpectedEofFailure(this, location)
            }
            token.type === this -> ParsedValue(token, fromIndex + 1)
            else -> MismatchTokenTypeFailure(token.location, this, token.type)
        }
    }

    override fun toString(): String = name
}
