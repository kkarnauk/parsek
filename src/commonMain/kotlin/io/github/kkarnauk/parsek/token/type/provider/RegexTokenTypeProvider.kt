package io.github.kkarnauk.parsek.token.type.provider

import io.github.kkarnauk.parsek.token.type.RegexTokenType

/**
 * Provides with [RegexTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [RegexTokenType].
 */
public fun regex(
    regex: String,
    options: Set<RegexOption> = setOf(),
    ignored: Boolean = false
): TokenTypeProvider<RegexTokenType> = object : TokenTypeProvider<RegexTokenType>() {
    override fun provide(name: String): RegexTokenType = RegexTokenType(regex, options, name, isIgnored)
}.ignored(ignored)
