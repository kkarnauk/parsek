# Parsek

[![Build and Tests](https://github.com/kkarnauk/parsek/actions/workflows/build_test.yml/badge.svg)](https://github.com/kkarnauk/parsek/actions/workflows/build_test.yml)

Parsek provides parser combinators (kombinators in fact) for Kotlin usages. With the library you can easily implement
parsers and lexers in Kotlin. It's a multiplatform library, so it can be used not only inside JVM projects, but also
inside Kotlin JS and Kotlin Native.


## Table of contents
* [Using](#using)
  * [Gradle](#gradle)
  * [Maven](#maven)
* [Examples](#examples)
  * [Parsing an integer](#parsing-an-integer)
  * [Parsing an arithmetic expression](#parsing-an-arithmetic-expression)
* [Tokens](#tokens)
  * [Types](#types)
  * [Type providers](#type-providers)
  * [Producers and tokenizers](#producers-and-tokenizers)
  * [Tokenizer providers](#tokenizer-providers)
* [Parsers](#parsers)
* [Grammars](#grammars)
* [Inspiration](#inspiration)

## Using

To use the library, it's enough to include the dependency from **Maven Central**.

### Gradle

```kotlin
dependencies {
    implementation("io.github.kkarnauk:parsek:0.1")
}
```

### Maven

```xml
<dependency>
  <groupId>io.github.kkarnauk</groupId>
  <artifactId>parsek</artifactId>
  <version>0.1</version>
</dependency>
```

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
val expression = ArithmeticExpressionGrammar.parse("1 + 2 ** 3 ** 4  * 12 + 3 * (1 + 2) ** 2 ** 3")
```

## Tokens

A [token](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/Token.kt) represents a matched part of an input.
A parser doesn't consume an initial input, it does consume a sequence of tokens.

Each token can tell you:
* The [type](#types)
* The input used to produce it
* The length of the matched part in the input
* The location where it was matched: the offset, row and column of the match in the input
* The substring of the input matched by the token

The important point is that you don't have to produce tokens on your own. 
This task is completed by [producers](#producers-and-tokenizers).

### Types

A [token type](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/TokenType.kt) 
introduces a family for tokens. 

For example, you may have an input `Hello, my friend!` a token type `any non-empty sequence of letters`.
Then there are several tokens with the type: `Hello`, `my` and `friend`.

Each token type can tell you:
* The name (that can be helpful while debugging)
* Whether tokens of that type are ignored by a parser: if `true`, then tokens are not passed to a parser
  (but they still consume the input!)

Each token type has the match method with the signature:
```kotlin
fun match(input: CharSequence, fromIndex: Int): Int
```
It starts matching `input` from `fromIndex` and returns the number of matched chars.
If the result is considered as successful if and only if the result is not 0.

The good news that lots of token types are already implemented. Check out:
* [Text](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/TextTokenType.kt). 
  Has parameters `text: String` and `ignoreCase: Boolean` 
  and matches only the strings that are equal to `text` up to `ignoreCase`.
* [Char](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/CharTokenType.kt). 
  Has parameters `char: Char` and `ignoreCase: Boolean`
  and matches only the string that are equal to single `char` up to `ignoreCase`.
* [Regex](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/RegexTokenType.kt). 
  Has parameters `regex: String` and `options: Set<RegexOption`.
  Compiles `regex` with `options` and adds something like `$` to make it match from the start.
* [Char predicate](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/CharPredicateTokenType.kt). 
  Accepts a lambda `(Char) -> Boolean` and matches chars while lambda returns `true`.
* [General token type](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/PredicateTokenType.kt). 
  There are two very general token types:
  * The first one accepts a lambda `(String, Int) -> Int` and it creates a token type with the match function
  replaced with the given lambda. 
  * The second token type accepts a lambda `(String) -> Int`, creates a view on the `input` from `fromIndex` in
  `match` and invokes the given lambda.

Also, there are even more good news. You don't have to name token types on your own.
For convenience, there are token type providers!

### Type providers

The purpose
of [TokenTypeProvider](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/type/provider/TokenTypeProvider.kt)
is very easy: we don't want to write extra information while creating new types. When we create a new token type, we
write it to a property, so, for example, there is already the name of the token type!
```kotlin
val digits by chars { it.isDigit() } 
```
In the example, we create a token type of type `Char predicate` (described above) and it now has the name `digits`.

Also, we've talked about ignoring tokens by parsers. If you want tokens of the specific type to be ignored by a parser,
you just go with one of the following ways:
```kotlin
val digits by regex("\\d+").ignored()
val digits by regex("\\d+", ignored = true)
```

So the providers help you to create tokens in a more convenient way.

**Note:** you must use **delegation** here in order to pass created tokens into a **tokenizer**.

Now, let's map the described token types into their providers:
* `Char ~~> by char(char: Char, ignoreCase: Boolean = false)`
* `Text ~~> by text(text: String, ignoreCase: Boolean = false)`
* `Regex ~~> by regex(regex: String, options: Set<RegexOption> = emptySet())`
* `Char predicate ~~> by chars(predicate: (Char) -> Boolean)`
* `General token type ~~> by tokenType(predicate: (String, Int) -> Int)`
* `General token type ~~> by tokenType(predicate: (String) -> Int`
* `General token type ~~> by tokenType(type: TokenType)`

Each provider takes the name of the created token type from the name of the property.

On each of those providers you can invoke `.ignored()` or pass `ignore = true` to them in order to make them
ignored by parsers!

### Producers and tokenizers

We've talked much about tokens and token types, 
but you still don't know how to convert a string into a collection of tokens.

There is an interface [TokenProducer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/TokenProducer.kt), 
which is responsible to provide tokens. The only interesting method it has is `nextToken(): Token?`. 
So, it either returns a new `Token`, or `null` if nothing can be produced.

But how to get a producer? For now, there is only one way to get it: 
[Tokenizer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/Tokenizer.kt). 
The main purpose of tokenizers is to take a string and turn it into a producer of tokens.

Usually, a tokenizer is initialized with a list of token types. These token types are retrieved automatically while you define them. 
Exactly for this purpose you must use delegation when creating a token type.

For now, there are two different implementations of a tokenizer:
* [Longest match tokenizer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/LongestMatchTokenizer.kt): 
  on each step tries to find a token type that matches the largest number of characters.
  After that turns the token type and current location in the input into a new token and returns it.
* [First match tokenizer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/FirstMatchTokenizer.kt): 
  almost the same, but takes the first token type with non-zero match result.

**The default tokenizer** is the longest match one.

Note that parsers accepts 
[IndexedTokenProcucer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/TokenProducer.kt), 
not the regular [Token producer](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/TokenProducer.kt).
The reason is that the regular token producers are lazy and there is no possibility to reuse the produced tokens.
The indexed producers memorize produced tokens and allow getting them by an index.

Anyway, you **don't need to implement an indexed producer** on your own. 
Each producer can be effectively turned into an indexed one by calling `producer.indexed()`.

### Tokenizer providers

As already mentioned above, tokenizers pull information about token types when you delegate token type providers.
If a tokenizer is initialized before all token types are initialized, it would not get all information.
It's not convenient to make users use `by lazy` or something like that on each override of `tokenizer`.

So there is a new abstraction: 
[TokenizerProvider](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/provider/TokenizerProvider.kt).
You give a list of token type, the provider gives a tokenizer. 

Now, the method for getting`a tokenizer for your [Grammar](#grammars) is **final** and lazy-implemented.
If you want to change a tokenizer for your grammar, override the method for getting a tokenizer provider.

There are implementations for providing default tokenizers:
[longest match](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/provider/LongestMatchTokenizerProvider.kt)
and
[first match](src/commonMain/kotlin/io/github/kkarnauk/parsek/token/tokenizer/provider/FirstMatchTokenizerProvider.kt).

## Parsers

TODO

## Grammars

TODO

## Inspiration
The project is inspired by an interesting library [better-parse](https://github.com/h0tk3y/better-parse) for the
combinators. I really liked that and decided to implement parser combinators on my own.
I'll try to make parsers better :)
