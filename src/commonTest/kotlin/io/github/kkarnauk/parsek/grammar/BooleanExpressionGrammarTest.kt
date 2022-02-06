package io.github.kkarnauk.parsek.grammar

import io.github.kkarnauk.parsek.exception.ParseException
import io.github.kkarnauk.parsek.exception.TokenizeException
import io.github.kkarnauk.parsek.grammar.BooleanExpression.*
import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.combinator.*
import io.github.kkarnauk.parsek.token.type.provider.char
import io.github.kkarnauk.parsek.token.type.provider.chars
import kotlin.test.Test

internal interface BooleanExpression {
    data class Variable(val name: String) : BooleanExpression
    data class Not(val expr: BooleanExpression) : BooleanExpression
    data class And(val left: BooleanExpression, val right: BooleanExpression) : BooleanExpression
    data class Or(val left: BooleanExpression, val right: BooleanExpression) : BooleanExpression
}

internal object BooleanExpressionGrammar : Grammar<BooleanExpression>() {
    private val id by chars { it.isLetter() }
    private val not by char('!')
    private val and by char('&')
    private val or by char('|')
    private val lpar by char('(')
    private val rpar by char(')')

    @Suppress("unused")
    private val ws by chars { it.isWhitespace() }.ignored()

    private val term: OrdinaryParser<BooleanExpression> = (id map { Variable(it.text) }) alt
            (-not seq ref(this::term) map { Not(it) }) alt
            (-lpar seq ref(this::parser) seq -rpar)

    private val andChain: OrdinaryParser<BooleanExpression> = leftAssociative(term, and) { res, cur -> And(res, cur) }
    private val orChain = leftAssociative(andChain, or) { res, cur -> Or(res, cur) }

    override val parser: OrdinaryParser<BooleanExpression>
        get() = orChain
}

internal class BooleanExpressionGrammarTest : AbstractGrammarTest<BooleanExpression, BooleanExpressionGrammar>() {
    override val grammar: BooleanExpressionGrammar
        get() = BooleanExpressionGrammar

    @Test
    fun testAnds() = doTest {
        text = "First & Second & Third"
        expected = And(And(Variable("First"), Variable("Second")), Variable("Third"))
    }

    @Test
    fun testOrs() = doTest {
        text = "a | a & b | a & (e | (b & b | c) | ((c & d)))"
        expected = Or(
            Or(Variable("a"), And(Variable("a"), Variable("b"))),
            And(
                Variable("a"),
                Or(
                    Or(
                        Variable("e"),
                        Or(And(Variable("b"), Variable("b")), Variable("c"))
                    ),
                    And(Variable("c"), Variable("d"))
                )
            )
        )
    }

    @Test
    fun testNots() = doTest {
        text = "!!(!  a &\t\t !!b | \n!c) & !d | ! !e"
        expected = Or(
            And(
                Not(
                    Not(
                        Or(And(Not(Variable("a")), Not(Not(Variable("b")))), Not(Variable("c"))),
                    )
                ),
                Not(Variable("d"))
            ),
            Not(Not(Variable("e")))
        )
    }

    @Test
    fun testFailTokenizingDigits() = doTestThrows<TokenizeException> {
        text = "a12 | b"
    }

    @Test
    fun testFailParsingNot() = doTestThrows<ParseException> {
        text = "a | !"
    }

    @Test
    fun testFailParsingAnd() = doTestThrows<ParseException> {
        text = "a | b & "
    }

    @Test
    fun testFailParsingTwoOrs() = doTestThrows<ParseException> {
        text = "a | | b"
    }

    @Test
    fun testFailParsingOr() = doTestThrows<ParseException> {
        text = "| b"
    }
}
