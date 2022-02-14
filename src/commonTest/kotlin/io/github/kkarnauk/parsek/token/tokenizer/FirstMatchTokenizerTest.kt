package io.github.kkarnauk.parsek.token.tokenizer

import io.github.kkarnauk.parsek.token.tokenizer.provider.TokenizerProvider
import io.github.kkarnauk.parsek.token.tokenizer.provider.firstMatchTokenizerProvider
import io.github.kkarnauk.parsek.token.type.TextTokenType
import kotlin.test.Test

@Suppress("UNUSED_VARIABLE")
internal class FirstMatchTokenizerTest : AbstractTokenizerTest<FirstMatchTokenizer>() {
    override val tokenizerProvider: TokenizerProvider<FirstMatchTokenizer>
        get() = firstMatchTokenizerProvider

    @Test
    fun testFirstButNotLongest() = doTest {
        text = "0000"
        val oneZero by TextTokenType("0", false, tokenTypeName(), false)
        val twoZeros by TextTokenType("00", false, tokenTypeName(), false)
        expectedTokenDescriptions = listOf(
            describeToken(oneZero, 0, 1),
            describeToken(oneZero, 1, 1),
            describeToken(oneZero, 2, 1),
            describeToken(oneZero, 3, 1)
        )
    }

    @Test
    fun testFirstAndLongest() = doTest {
        text = "aaaaa"
        val aa by TextTokenType("aa", false, tokenTypeName(), false)
        val a by TextTokenType("a", false, tokenTypeName(), false)
        expectedTokenDescriptions = listOf(
            describeToken(aa, 0, 2),
            describeToken(aa, 2, 2),
            describeToken(a, 4, 1)
        )
    }
}
