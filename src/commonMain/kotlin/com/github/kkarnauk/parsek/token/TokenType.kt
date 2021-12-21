package com.github.kkarnauk.parsek.token

/**
 * Represents different tokens before tokenizing. After that, they transform into [Token].
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
