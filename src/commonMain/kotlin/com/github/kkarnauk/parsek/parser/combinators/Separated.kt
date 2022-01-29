package com.github.kkarnauk.parsek.parser.combinators

import com.github.kkarnauk.parsek.parser.*
import com.github.kkarnauk.parsek.token.IndexedTokenProducer

private class SeparatedCombinator<T, S, R>(
    private val item: OrdinaryParser<T>,
    private val separator: OrdinaryParser<S>,
    private val allowEmpty: Boolean,
    private val transform: (List<T>, List<S>) -> R
) : OrdinaryParser<R> {
    override fun parse(tokenProducer: IndexedTokenProducer, fromIndex: Int): ParseResult<R> {
        val items = mutableListOf<T>()
        val separators = mutableListOf<S>()
        var nextIndex = fromIndex
        var returnIndex = fromIndex
        while (true) {
            when (val itemResult = item.parse(tokenProducer, nextIndex)) {
                is SuccessfulParse -> {
                    items += itemResult.value
                    returnIndex = itemResult.nextTokenIndex
                    when (val separatorResult = separator.parse(tokenProducer, itemResult.nextTokenIndex)) {
                        is SuccessfulParse -> {
                            separators += separatorResult.value
                            nextIndex = separatorResult.nextTokenIndex
                        }
                        is ParseFailure -> return ParsedValue(transform(items, separators), itemResult.nextTokenIndex)
                    }
                }
                is ParseFailure -> {
                    return if (items.isEmpty() && !allowEmpty) itemResult else {
                        if (separators.isNotEmpty()) {
                            separators.removeLast()
                        }
                        ParsedValue(transform(items, separators), returnIndex)
                    }
                }
            }
        }
    }
}

/**
 * @return [OrdinaryParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * If the last element is [separator], then it will not be used.
 * If [allowEmpty] is `true`, then no parsers may be applied.
 */
public fun <T> separated(
    item: OrdinaryParser<T>,
    separator: Parser<*>,
    allowEmpty: Boolean = false
): OrdinaryParser<List<T>> = SeparatedCombinator(item, separator.toOrdinary(), allowEmpty) { x, _ -> x }

/**
 * @return [SkipParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * If the last element is [separator], then it will not be used.
 * If [allowEmpty] is `true`, then no parsers may be applied.
 */
public fun <T> separated(
    item: SkipParser<T>,
    separator: Parser<*>,
    allowEmpty: Boolean = false
): SkipParser<List<T>> = separated(item.inner, separator, allowEmpty).skip()

/**
 * @return [OrdinaryParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * After that it folds the result from the left to the right.
 * If the last applied parser is [separator] then it is ignored.
 *
 * It is important that the initial value is `items[0]`,
 * on i-th step the result is `transform(last_res, items[`i`], separators[i - 1])`.
 */
public fun <T : R, S, R> leftAssociative(
    item: OrdinaryParser<T>,
    separator: Parser<S>,
    transform: (R, T, S) -> R
): OrdinaryParser<R> = SeparatedCombinator(item, separator.toOrdinary(), false) { items, separators ->
    var result: R = items.first()
    for (i in 1 until items.size) {
        result = transform(result, items[i], separators[i - 1])
    }
    result
}

/**
 * @return [OrdinaryParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * After that it folds the result from the left to the right, ignoring [separator].
 */
public fun <T : R, R> leftAssociative(
    item: OrdinaryParser<T>,
    separator: Parser<*>,
    transform: (R, T) -> R
): OrdinaryParser<R> = leftAssociative(item, separator) { res: R, value: T, _ -> transform(res, value) }

/**
 * @return [OrdinaryParser] that applies [item] as many as possible.
 * After that it folds the result from the left to the right.
 */
public fun <T : R, R> leftAssociative(
    item: OrdinaryParser<T>,
    transform: (R, T) -> R
): OrdinaryParser<R> = leftAssociative(item, emptyParser(), transform)

/**
 * @return [OrdinaryParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * After that it folds the result from the right to the left.
 * If the last applied parser is [separator] then it is ignored.
 *
 * It is important that the initial value is `items.last()`,
 * on i-th step the result is `transform(items[`i`], separators[`i`], last_res)`.
 */
public fun <T : R, S, R> rightAssociative(
    item: OrdinaryParser<T>,
    separator: Parser<S>,
    transform: (T, S, R) -> R
): OrdinaryParser<R> = SeparatedCombinator(item, separator.toOrdinary(), false) { items, separators ->
    var result: R = items.last()
    for (i in items.size - 2 downTo 0) {
        result = transform(items[i], separators[i], result)
    }
    result
}

/**
 * @return [OrdinaryParser] that applies [item], [separator], [item], [separator] and so on as many as possible.
 * After that it folds the result from the right to the left, ignoring [separator].
 */
public fun <T : R, R> rightAssociative(
    item: OrdinaryParser<T>,
    separator: Parser<*>,
    transform: (T, R) -> R
): OrdinaryParser<R> = rightAssociative(item, separator) { value: T, _, res: R -> transform(value, res) }

/**
 * @return [OrdinaryParser] that applies [item] as many as possible.
 * After that it folds the result from the right to the left.
 */
public fun <T : R, R> rightAssociative(
    item: OrdinaryParser<T>,
    transform: (T, R) -> R
): OrdinaryParser<R> = rightAssociative(item, emptyParser(), transform)
