package io.github.kkarnauk.parsek.token.type

/**
 * Represents a token that tries to match the given regex from an index in an input.
 */
public class RegexTokenType(
    regex: String,
    options: Set<RegexOption>,
    name: String,
    ignored: Boolean
) : AbstractTokenType(name, ignored) {
    private val matcher = "^$regex".toRegex(options)

    override fun match(input: CharSequence, fromIndex: Int): Int {
        val match = matcher.find(input.subSequence(fromIndex, input.length)) // TODO temporary solution
        return if (match != null) match.range.last - match.range.first + 1 else 0
    }
}
