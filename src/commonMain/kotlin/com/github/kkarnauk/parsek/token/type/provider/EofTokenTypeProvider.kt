package com.github.kkarnauk.parsek.token.type.provider

import com.github.kkarnauk.parsek.token.type.EofTokenType

/**
 * Provides with [EofTokenType].
 */
public val eofToken: TokenTypeProvider<EofTokenType> = object : TokenTypeProvider<EofTokenType>() {
    override fun provide(name: String): EofTokenType = EofTokenType
}
