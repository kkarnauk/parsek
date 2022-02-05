# Parsek

[![Build and Tests](https://github.com/kkarnauk/parsek/actions/workflows/build_test.yml/badge.svg)](https://github.com/kkarnauk/parsek/actions/workflows/build_test.yml)

Parsek provides parser combinators (kombinators in fact) for Kotlin usages. With the library you can easily implement
parsers and lexers in Kotlin. It's a multiplatform library, so it can be used not only inside JVM projects, but also
inside Kotlin JS and Kotlin Native.


## Table of contents
* [Installation](#installation)
* [Examples](#examples)
  * [Parsing an integer](#parsing-an-integer)
  * [Parsing an arithmetic expression](#parsing-an-arithmetic-expression)

## Installation

**TODO (it's not published yet)**

## Examples

### Parsing an integer

Let's start with an easy task. For instance, you want to parse an integer. There are several ways to do that.
The first one:
```kotlin
object IntegerGrammar : Grammar<Int>() {
    val digits by chars { it.isDigit() } // define a token for sequence of digits
    val minus by char('-') // define a token for a minus
    
    val positiveInteger by digits map { it.text.toInt() } // parser for a positive integer 
    val integer by (-minus seq positiveInteger map { -it }) alt positiveInteger // parser for an integer
    
    override val parser by integer
}
```
The second one is much simpler:
```kotlin
object IntegerGrammar : Grammar<Int>() {
    private val integer by regex("-?\\d+") // define a token for an entire integer
    
    override val parser by integer map { it.text.toInt() } // map a text into an integer
}
```
And now, if you want to parse an integer in your program, you can just do something like that:
```kotlin
val integer = IntegerGrammar.parse("-42")
```

### Parsing an arithmetic expression

Yes, that's too simple an example, isn't it? Let's try something bigger. For instance, you want to parse an arithmetic
expression with summing, multiplying, powering and parenthesis. So:
```kotlin
object ArithmeticExpressionGrammar : Grammar<ArithmeticExpression>() {
    val num by regex("-?\\d+")
    val sum by char('+')
    val mul by char('*')
    val pow by text("**")
    val lpar by char('(')
    val rpar by char(')')
    
    val whitespaces by chars { it.isWhitespace() }.ignored()
    
    val primary by (num map { Value(it.text.toInt()) }) alt (-lpar seq ref(::parser) seq -rpar)
    val pows by rightAssociative(primary, pow) map { cur, res -> Power(cur, res) }
    val mults by leftAssociative(pows, mul) map { res, cur -> Mul(cur, res) }
    val sums by leftAssociative(mults, sum) map { res, cur -> Sum(cur, res) }
    
    override val parser by sums
}
```
There are some, maybe, not obvious things like the unary minuses. So let's go into it.
* First, we declared the main tokens. Note, that we've used `ignored()` on the `whitespaces`-token,
  so this token will not be passed to the parser, therefore we don't need to care about all the whitespaces.
* After that, we've declared the `primary`-rule. There are actually two different rules, separated by
  the `alt`-combinator, that means "First, I'll try the first parser, and if it fails, I'll try the another one".
* The `map`-combinator transforms the result under the parser, if it succeeds.
* The `seq`-combinator tells "To succeed, I need the first parser to be successful and after that the another one.".
  The unary minus tells that we don't care about the result of the parser and it must be skipped, if is succeeds.
  If we didn't use the unary minus, then we would have `Pair<T, S>` as the result of successful parsing.
* The `ref`-combinator is used to take not-initialized parsers. We cannot directly use `parser`, because
  it isn't initialized yet.
* The `rightAssociative` and `leftAssociative` are used to perform `foldr` and `foldl` on the results of
  consecutive parsers `item`, `separator`, `item` and so on. For example, `item=primary` and `separator=pow` here.

After it's all done, we can use the grammar in the following way:
```kotlin
val expression = ArithmeticExpression.parse("1 + 2 ** 3 ** 4  * 12 + 3 * (1 + 2) ** 2 ** 3")
```

## Inspiration
The project is inspired by an interesting library [better-parse](https://github.com/h0tk3y/better-parse) for the
combinators. I really liked that and decided to implement parser combinators on my own.
I'll try to make parsers better :)
