package com.github.kkarnauk.parsek.parser

import com.github.kkarnauk.parsek.token.IndexedTokenProducer

public interface Parser<out T> {
    public fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<T>
}

public interface OrdinaryParser<out T> : Parser<T>

public interface SkipParser<out T> : Parser<T>
