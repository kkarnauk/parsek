package com.github.kkarnauk.parsek.token.types

public class RegexTokenType(
    regex: String,
    options: Set<RegexOption>,
    name: String,
    ignored: Boolean
) : AbstractTokenType(name, ignored) {
    private val matcher = "^$regex".toRegex(options)

    override fun match(input: CharSequence, fromIndex: Int): Int {
        val match = matcher.find(input, fromIndex)
        return if (match != null) match.range.last - match.range.first else 0
    }
}