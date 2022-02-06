package io.github.kkarnauk.parsek.token.type

/**
 * Represents a token that tries to match [text] from an index in an input.
 * If [ignoreCase] is `true` then case is ignored while matching.
 * Match is considered as successful iff the whole [text] is matched.
 */
public class TextTokenType(
    private val text: CharSequence,
    private val ignoreCase: Boolean,
    name: String,
    ignored: Boolean
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int =
        if (input.startsWith(text, fromIndex, ignoreCase)) text.length else 0
}
