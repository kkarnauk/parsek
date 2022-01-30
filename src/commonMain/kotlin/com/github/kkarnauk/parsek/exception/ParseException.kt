package com.github.kkarnauk.parsek.exception

import com.github.kkarnauk.parsek.parser.ParseFailure

public class ParseException(message: String) : Exception(message)

public fun ParseFailure.toException(): ParseException = ParseException(message)
