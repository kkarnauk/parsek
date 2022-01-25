package com.github.kkarnauk.parsek.token

/**
 * Required to transform a text into [TokenProducer].
 */
public interface Tokenizer {
    /**
     * Transforms [input] into [TokenProducer].
     */
    public fun tokenize(input: CharSequence): TokenProducer
}
