package com.github.kkarnauk.parsek.grammar

import com.github.kkarnauk.parsek.parser.OrdinaryParser
import com.github.kkarnauk.parsek.parser.combinator.*
import com.github.kkarnauk.parsek.token.type.provider.char
import kotlin.test.Test

internal object CorrectBracketSequenceGrammar : Grammar<Boolean>() {
    private val lpar by char('(')
    private val rpar by char(')')
    private val lbracket by char('[')
    private val rbracket by char(']')

    private val any = (lpar alt rpar alt lbracket alt rbracket) map { false }

    private val inside: OrdinaryParser<Boolean> =
            (-lpar seq ref(::sequence) seq -rpar) alt
            (-lbracket seq ref(::sequence) seq -rbracket)

    private val sequence = inside.zeroOrMore() map { true }

    private val success = sequence
    private val failure = any.oneOrMore() map { false }

    override val parser: OrdinaryParser<Boolean>
        get() = (success seq -eofParser()) alt failure
}

internal class CorrectBracketSequenceCheckerTest : AbstractGrammarTest<Boolean, CorrectBracketSequenceGrammar>() {
    override val grammar: CorrectBracketSequenceGrammar
        get() = CorrectBracketSequenceGrammar

    private fun doCorrectTest(block: InsideTest.() -> Unit) = doTest {
        block()
        expected = true
    }

    private fun doIncorrectTest(block: InsideTest.() -> Unit) = doTest {
        block()
        expected = false
    }

    @Test
    fun testCorrect() {
        doCorrectTest { text = "" }
        doCorrectTest { text = "(([]))" }
        doCorrectTest { text = "([()](())[()()])()[[]]" }
        doCorrectTest { text = "()()()()()()()[[]]()[[()[][]]()()]" }
    }

    @Test
    fun testIncorrect() {
        doIncorrectTest { text = "(" }
        doIncorrectTest { text = ")" }
        doIncorrectTest { text = "([)" }
        doIncorrectTest { text = "([)]" }
        doIncorrectTest { text = "[)" }
        doIncorrectTest { text = "(())())" }
    }

    @Test
    fun testBigCorrect() {
        doCorrectTest { text = "(".repeat(100) + ")".repeat(100) }
        doCorrectTest { text = "([][])".repeat(3000) }
        doCorrectTest { text = "(()[][[]])([()])[[]]".repeat(700) }
    }
}
