package com.github.kkarnauk.parsek.token.types

/**
 * Substitute [predicate] into [match]. Use this only if you are very accurate with consumed memory.
 * Otherwise, see [predicateTokenType].
 */
public class PredicateTokenType(
    name: String,
    ignored: Boolean,
    private val predicate: (input: CharSequence, fromIndex: Int) -> Int
) : AbstractTokenType(name, ignored) {
    override fun match(input: CharSequence, fromIndex: Int): Int = predicate(input, fromIndex)
}

/**
 * @return [PredicateTokenType] but you can assume that `fromIndex` is equal to 0 (i.e. it's a start of an input).
 */
public fun predicateTokenType(
    name: String,
    ignored: Boolean,
    predicate: (input: CharSequence) -> Int
): PredicateTokenType = PredicateTokenType(name, ignored) { input, fromIndex ->
    predicate(charSequenceView(input, fromIndex))
}

private fun charSequenceView(charSequence: CharSequence, fromIndex: Int): CharSequence = object : CharSequence {
    override val length: Int = charSequence.length - fromIndex

    override fun get(index: Int): Char = charSequence[index + fromIndex]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return charSequence.subSequence(startIndex + fromIndex, endIndex + fromIndex)
    }
}
