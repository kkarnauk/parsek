package com.github.kkarnauk.parsek.token.type

/**
 * Represents a token that tries to check [predicate] on each character one by one.
 * Once [predicate] fails, the matching ends and returns the number of matched chars.
 */
public class CharPredicateTokenType(
    name: String,
    ignored: Boolean,
    private val predicate: (Char) -> Boolean
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        val length = input.length
        for (i in fromIndex until length) {
            if (!predicate(input[i])) return i - fromIndex
        }
        return length - fromIndex
    }
}
