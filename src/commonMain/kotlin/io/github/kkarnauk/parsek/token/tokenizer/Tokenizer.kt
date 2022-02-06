package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.token.TokenProducer

/**
 * Required to transform a text into [TokenProducer].
 */
public interface Tokenizer {
    /**
     * Transforms [input] into [TokenProducer].
     */
    public fun tokenize(input: CharSequence): TokenProducer
}
