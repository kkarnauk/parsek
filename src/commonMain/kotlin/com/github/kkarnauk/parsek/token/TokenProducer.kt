package com.github.kkarnauk.parsek.token

/**
 * Produces tokens one by one. For example, tokenizer is a tokens' producer.
 */
public interface TokenProducer {
    /**
     * Tries to produce a new token. If no tokens left, returns `null`.
     */
    public fun nextToken(): Token?
}

/**
 * Tokens' producer that allows getting a token by an index.
 */
public interface IndexedTokenProducer : TokenProducer {
    /**
     * Returns a token by an index.
     */
    public operator fun get(index: Int): Token
}

/**
 * Transforms a token producer into an indexed one (if needed).
 */
public fun TokenProducer.indexed(): IndexedTokenProducer = when (this) {
    is IndexedTokenProducer -> this
    else -> object : IndexedTokenProducer {
        private val tokens = mutableListOf<Token>()

        override fun nextToken(): Token? = this@indexed.nextToken()?.also { tokens += it }

        override fun get(index: Int): Token {
            require(index >= 0) { "Index must be non-negative." }
            while (tokens.size < index + 1) {
                requireNotNull(nextToken()) { "Cannot get a token by index $index: too big." }
            }
            return tokens[index]
        }
    }
}
