package com.github.kkarnauk.parsek.token.types

/**
 * Represents different tokens before tokenizing.
 */
public interface TokenType {
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
) : TokenType
