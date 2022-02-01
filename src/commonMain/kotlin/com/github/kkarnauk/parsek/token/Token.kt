package com.github.kkarnauk.parsek.token

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.type.TokenType

/**
 * Represents a result of [TokenProducer].
 */
public data class Token(
    /**
     * Type of the produced token.
     */
    public val type: TokenType,
    /**
     * Input from which the token was produced.
     */
    public val input: CharSequence,
    /**
     * Length of matched part.
     */
    public val length: Int,
    /**
     * Location of the produced token.
     */
    public val location: Location
) {
    /**
     * Part of [input] that matched with the token. Its length is equal to [length].
     */
    public val text: String get() = input.substring(location.offset, location.offset + length)
}
