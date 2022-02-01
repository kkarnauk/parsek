package com.github.kkarnauk.parsek.token.type

/**
 * Represents a token that compares first character with [char] taking into account [ignoreCase].
 */
public class CharTokenType(
    private val char: Char,
    private val ignoreCase: Boolean,
    name: String,
    ignored: Boolean
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        return if (input[fromIndex].equals(char, ignoreCase)) 1 else 0
    }
}
