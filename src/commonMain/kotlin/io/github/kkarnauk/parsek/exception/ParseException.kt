package io.github.kkarnauk.parsek.exception

import io.github.kkarnauk.parsek.parser.ParseFailure

/**
 * Represents [Exception] that may be thrown from a parser.
 */
public open class ParseException(message: String) : Exception(message)

/**
 * Transforms [ParseFailure] into [ParseException] using [ParseFailure.message].
 */
public fun ParseFailure.toException(): ParseException = ParseException(message)
