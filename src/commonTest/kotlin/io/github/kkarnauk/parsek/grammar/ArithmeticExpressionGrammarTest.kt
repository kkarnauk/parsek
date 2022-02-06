package io.github.kkarnauk.parsek.grammar

import io.github.kkarnauk.parsek.exception.ParseException
import io.github.kkarnauk.parsek.exception.TokenizeException
import io.github.kkarnauk.parsek.grammar.ArithmeticExpression.*
import io.github.kkarnauk.parsek.parser.OrdinaryParser
import io.github.kkarnauk.parsek.parser.combinator.*
import io.github.kkarnauk.parsek.token.type.provider.char
import io.github.kkarnauk.parsek.token.type.provider.chars
import io.github.kkarnauk.parsek.token.type.provider.regex
import io.github.kkarnauk.parsek.token.type.provider.text
import kotlin.test.Test

internal sealed interface ArithmeticExpression {
    data class Value(val value: Int) : ArithmeticExpression
    data class UnaryMinus(val expr: ArithmeticExpression) : ArithmeticExpression
    data class Sum(val left: ArithmeticExpression, val right: ArithmeticExpression) : ArithmeticExpression
    data class Sub(val left: ArithmeticExpression, val right: ArithmeticExpression) : ArithmeticExpression
    data class Mul(val left: ArithmeticExpression, val right: ArithmeticExpression) : ArithmeticExpression
    data class Div(val left: ArithmeticExpression, val right: ArithmeticExpression) : ArithmeticExpression
    data class Pow(val base: ArithmeticExpression, val exp: ArithmeticExpression) : ArithmeticExpression
}

internal class ArithmeticExpressionGrammar(
    private val variables: Map<String, Int>
) : Grammar<ArithmeticExpression>() {
    private val num by regex("\\d+")
    private val variable by chars { it.isLetter() }
    private val sum by char('+')
    private val sub by char('-')
    private val mul by char('*')
    private val div by char('/')
    private val pow by text("**")
    private val lpar by char('(')
    private val rpar by char(')')

    @Suppress("unused")
    private val ws by chars { it.isWhitespace() }.ignored()

    private val primary: OrdinaryParser<ArithmeticExpression> = (num map { Value(it.text.toInt()) } ) alt
            (variable map { Value(variables.getValue(it.text)) }) alt
            (-lpar seq ref(this::parser) seq -rpar) alt
            (-sub seq ref(this::primary) map { UnaryMinus(it) })

    private val powers = rightAssociative(primary, pow) { cur, _, res -> Pow(cur, res) }
    private val mults = leftAssociative(powers, mul alt div) { res, cur, op ->
        if (op.type === mul) Mul(res, cur) else Div(res, cur)
    }
    private val sums = leftAssociative(mults, sum alt sub) { res, cur, op ->
        if (op.type === sum) Sum(res, cur) else Sub(res, cur)
    }

    override val parser: OrdinaryParser<ArithmeticExpression>
        get() = sums
}

internal class ArithmeticExpressionGrammarTest :
    AbstractGrammarTest<ArithmeticExpression, ArithmeticExpressionGrammar>() {

    private val variables = mapOf("ten" to 10, "x" to 42, "one" to 1, "zero" to 0, "minusOne" to -1)

    override val grammar: ArithmeticExpressionGrammar = ArithmeticExpressionGrammar(variables)

    @Test
    fun testVariable() = doTest {
        text = "x + 2"
        expected = Sum(Value(42), Value(2))
    }

    @Test
    fun testNonExistentVariable() = doTestThrows<NoSuchElementException> {
        text = "y - 2"
    }

    @Test
    fun testUnaryMinus() = doTest {
        text = "---1 + -2 * -3 / (-2 - 3)"
        expected = Sum(
            UnaryMinus(UnaryMinus(UnaryMinus(Value(1)))),
            Div(
                Mul(UnaryMinus(Value(2)), UnaryMinus(Value(3))),
                Sub(UnaryMinus(Value(2)), Value(3))
            )
        )
    }

    @Test
    fun testMultsAndDivs() = doTest {
        text = "1 + 20 / 30 / 40 * 1 / 2 / 3 / 4 * -1"
        expected = Sum(
            Value(1),
            Mul(
                Div(
                    Div(
                        Div(
                            Mul(Div(Div(Value(20), Value(30)), Value(40)), Value(1)),
                            Value(2)
                        ),
                        Value(3)
                    ),
                    Value(4)
                ),
                UnaryMinus(Value(1))
            )
        )
    }

    @Test
    fun testSumsAndMinuses() = doTest {
        text = "1 + 2 - 3 - 4 - 5"
        expected = Sub(Sub(Sub(Sum(Value(1), Value(2)), Value(3)), Value(4)), Value(5))
    }

    @Test
    fun testComplexExpression() = doTest {
        text = "1 ** 2**33**(2 + 3**3)**533 * ten  \n ** (minusOne * -21 ** 3 ** 4 * (((3))) + 42) / (-2 + 3) + 21"
        expected = Sum(
            Div(
                Mul(
                    Pow(
                        Value(1),
                        Pow(
                            Value(2),
                            Pow(
                                Value(33),
                                Pow(Sum(Value(2), Pow(Value(3), Value(3))), Value(533))
                            )
                        )
                    ),
                    Pow(
                        Value(10),
                        Sum(
                            Mul(
                                Mul(
                                    Value(-1),
                                    Pow(UnaryMinus(Value(21)), Pow(Value(3), Value(4)))
                                ),
                                Value(3)
                            ),
                            Value(42)
                        )
                    )
                ),
                Sum(UnaryMinus(Value(2)), Value(3))
            ),
            Value(21)
        )
    }

    @Test
    fun testFailIncorrectExpression() {
        doTestThrows<ParseException> { text = "1 * " }
        doTestThrows<ParseException> { text = "* 2" }
        doTestThrows<ParseException> { text = "1 / 2 /" }
        doTestThrows<ParseException> { text = "1 + *2" }
        doTestThrows<ParseException> { text = "1 + 2 + (3 - 2" }
        doTestThrows<ParseException> { text = "1 + 2)" }
        doTestThrows<ParseException> { text = "1 - (2 +)" }
        doTestThrows<ParseException> { text = "1 + (2 +) 3" }
        doTestThrows<ParseException> { text = "1 (2 / 3)" }
    }

    @Test
    fun testFailTokenizing() {
        doTestThrows<TokenizeException> { text = "1 \\ 2" }
        doTestThrows<TokenizeException> { text = "!2" }
        doTestThrows<TokenizeException> { text = "1 ^ 2 ^ 3" }
    }
}
