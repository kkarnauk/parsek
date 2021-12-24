package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.types.EofTokenType
import com.github.kkarnauk.parsek.token.types.TokenType

public sealed interface ParseResult<out T>

public interface SuccessfulParse<out T> : ParseResult<T> {
    public val value: T
    public val nextTokenIndex: Int
}

public data class ParsedValue<out T>(
    override val value: T,
    override val nextTokenIndex: Int
) : SuccessfulParse<T>

public interface ParseFailure : ParseResult<Nothing> {
    public val message: String
}

public interface LocatedParseError : ParseFailure {
    public val location: Location
}

public data class MismatchTokenTypeFailure(
    override val location: Location,
    private val expected: TokenType,
    private val actual: TokenType
) : LocatedParseError {
    override val message: String
        get() = "Token mismatch at $location: expected=$expected, actual=$actual."
}

public fun unexpectedEofFailure(expected: TokenType, location: Location): LocatedParseError {
    return MismatchTokenTypeFailure(location, expected, EofTokenType)
}
