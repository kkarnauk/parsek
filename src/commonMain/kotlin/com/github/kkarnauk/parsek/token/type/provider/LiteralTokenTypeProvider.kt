package com.github.kkarnauk.parsek.token.type.provider

import com.github.kkarnauk.parsek.token.type.TextTokenType

/**
 * Provides with [TextTokenType] by a name.
 * Required to take names from properties when constructing a grammar and provide with [TextTokenType].
 */
public fun text(
    text: String,
    ignoreCase: Boolean = false,
    ignored: Boolean = false
): TokenTypeProvider<TextTokenType> = object : TokenTypeProvider<TextTokenType>() {
    override fun provide(name: String): TextTokenType = TextTokenType(text, ignoreCase, name, isIgnored)
}.ignored(ignored)
