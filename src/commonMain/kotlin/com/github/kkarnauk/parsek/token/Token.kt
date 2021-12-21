package com.github.kkarnauk.parsek.token

import com.github.kkarnauk.parsek.token.types.TokenType

public data class Token(
    public val type: TokenType,
    public val index: Int,
    public val input: CharSequence,
    public val length: Int,
    public val location: Location
) {
    public val text: String get() = input.substring(location.offset, location.offset + length)

    public class Location(
        public val offset: Int,
        public val row: Int,
        public val column: Int
    )
}
