package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.info.Location
import com.github.kkarnauk.parsek.token.types.EofTokenType
import com.github.kkarnauk.parsek.token.types.TokenType

/**
 * Result of parsing with type [T].
 */
public sealed interface ParseResult<out T>

/**
 * Represents a success while parsing.
 * Common usage: when you get [ParseResult], check whether it is [SuccessfulParse] or [ParseFailure].
 */
public interface SuccessfulParse<out T> : ParseResult<T> {
    /**
     * Directly the result of parsing.
     */
    public val value: T

    /**
     * Next token index to parse after this success.
     */
    public val nextTokenIndex: Int
}

/**
 * Default successful result.
 */
public data class ParsedValue<out T>(
    override val value: T,
    override val nextTokenIndex: Int
) : SuccessfulParse<T>

/**
 * Represents a failure while parsing.
 * Common usage: when you get [ParseResult], check whether it is [SuccessfulParse] or [ParseFailure].
 */
public interface ParseFailure : ParseResult<Nothing> {
    /**
     * Diagnostic message in order to understand why parsing failed.
     * You must provide it!
     */
    public val message: String
}

/**
 * Represents a failure while parsing with knowing exact location where it failed.
 */
public interface LocatedParseError : ParseFailure {
    /**
     * Directly the location of failure.
     */
    public val location: Location
}

/**
 * Represents a failure at [location] when a parser expected token [expected] but got [actual].
 */
public data class MismatchTokenTypeFailure(
    override val location: Location,
    private val expected: TokenType,
    private val actual: TokenType
) : LocatedParseError {
    override val message: String
        get() = "Token mismatch at $location: expected=$expected, actual=$actual."
}

/**
 * Represents a failure at [location] when a parser expected token [expected] but got EOF.
 */
public fun unexpectedEofFailure(expected: TokenType, location: Location): LocatedParseError {
    return MismatchTokenTypeFailure(location, expected, EofTokenType)
}
