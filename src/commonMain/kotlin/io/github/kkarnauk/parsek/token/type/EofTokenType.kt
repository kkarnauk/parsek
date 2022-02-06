package io.github.kkarnauk.parsek.token.type

/**
 * Indicates that EOF is found. This token should never appear while parsing.
 */
public object EofTokenType : AbstractTokenType("EOF", false) {
    override fun match(input: CharSequence, fromIndex: Int): Int {
        throw IllegalStateException("EOF token must not be used to be matched.")
    }
}
