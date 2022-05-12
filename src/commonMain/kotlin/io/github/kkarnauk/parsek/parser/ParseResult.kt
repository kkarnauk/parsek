package io.github.kkarnauk.parsek.parser

import io.github.kkarnauk.parsek.info.Location
import io.github.kkarnauk.parsek.token.Token
import io.github.kkarnauk.parsek.token.type.EofTokenType
import io.github.kkarnauk.parsek.token.type.TokenType

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
public interface LocatedParseFailure : ParseFailure {
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
) : LocatedParseFailure {
    override val message: String
        get() = "Token mismatch at $location: expected=$expected, actual=$actual."
}

/**
 * Represents a failure at [location] when a parser expected token [expected] but got EOF.
 */
public fun unexpectedEofFailure(location: Location, expected: TokenType): LocatedParseFailure {
    return MismatchTokenTypeFailure(location, expected, EofTokenType)
}

public data class NoSuchAlternativeFailure(
    override val location: Location,
    private val alternativeFailures: List<ParseFailure>
) : LocatedParseFailure {
    // TODO nicer message
    override val message: String
        get() = "No such appropriate alternative at $location:\n" +
                alternativeFailures.joinToString("\n") { it.message }
}

public data class UnparsedRemainderFailure(
    private val nextToken: Token
) : LocatedParseFailure {
    override val location: Location
        get() = nextToken.location

    override val message: String
        get() = "The parser matched only the part of the input. " +
                "Stopped at $location on the token named ${nextToken.type.name}."
}
