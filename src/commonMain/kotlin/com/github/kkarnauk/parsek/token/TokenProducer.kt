package com.github.kkarnauk.parsek.token

/**
 * Produces tokens one by one. For example, tokenizer is a tokens' producer.
 */
public interface TokenProducer : Iterable<Token> {
    /**
     * Tries to produce a new token. If no tokens left, returns `null`.
     */
    public fun nextToken(): Token?

    override fun iterator(): Iterator<Token> = object : AbstractIterator<Token>() {
        override fun computeNext() {
            when (val value = nextToken()) {
                null -> done()
                else -> setNext(value)
            }
        }
    }
}

/**
 * Tokens' producer that allows getting a token by an index.
 */
public interface IndexedTokenProducer : TokenProducer {
    /**
     * Returns a token by an index.
     */
    public operator fun get(index: Int): Token

    /**
     * Returns a token by index or null if [index] is out of range.
     */
    public fun getOrNull(index: Int): Token?

    /**
     * Returns the last produced token or `null` if nothing was produced.
     */
    public val lastToken: Token?
}

/**
 * Transforms a token producer into an indexed one (if needed).
 */
public fun TokenProducer.indexed(): IndexedTokenProducer = when (this) {
    is IndexedTokenProducer -> this
    else -> object : IndexedTokenProducer {
        private val tokens = mutableListOf<Token>()

        override val lastToken: Token? = tokens.lastOrNull()

        override fun nextToken(): Token? = this@indexed.nextToken()?.also { tokens += it }

        override fun get(index: Int): Token {
            require(index >= 0) { "Index must be non-negative." }
            return requireNotNull(getOrNull(index)) {
                "Cannot get a token by index $index: too big."
            }
        }

        override fun getOrNull(index: Int): Token? {
            if (index < 0) {
                return null
            }
            while (tokens.size < index + 1) {
                if (nextToken() == null) {
                    break
                }
            }
            return tokens.getOrNull(index)
        }
    }
}
