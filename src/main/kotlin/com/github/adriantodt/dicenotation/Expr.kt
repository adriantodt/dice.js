package com.github.adriantodt.dicenotation

import com.github.adriantodt.tartar.api.lexer.Section
import com.github.adriantodt.tartar.api.lexer.Sectional

sealed class Expr : Sectional {
    data class Dice(override val section: Section, val rolls: Int, val sides: Int) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString() = "${rolls}d$sides"
    }

    data class SolvedDice(val dice: Dice, val values: List<Int>) : Expr(), Sectional by dice {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        val result = values.sum()

        override fun toString(): String {
            return values.joinToString(", ", "[", "]" + dice) {
                if (it == 1 || it == dice.sides) "<b>$it</b>" else it.toString()
            }
        }
    }

    data class Integer(override val section: Section, val value: Int) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString() = value.toString()
    }

    data class Decimal(override val section: Section, val value: Double) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString() = value.toString()
    }

    data class BinaryOp(override val section: Section, val left: Expr, val right: Expr, val type: Type) : Expr() {
        enum class Type(val operator: String) {
            PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"),
            MODULUS("%"),POWER("^"), SHL("<<"), SHR(">>");
        }

        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString(): String {
            val sb = StringBuilder()
            val prec = Precedence.of(type)
            val leftPrec = Precedence.of(left)
            val rightPrec = Precedence.of(right)
            if (leftPrec > prec) {
                sb.append('(').append(left).append(')')
            } else {
                sb.append(left)
            }
            sb.append(' ').append(type.operator).append(' ')
            if (rightPrec > prec) {
                sb.append('(').append(right).append(')')
            } else {
                sb.append(right)
            }
            return sb.toString()
        }
    }

    data class UnaryOp(override val section: Section, val target: Expr, val type: Type) : Expr() {
        enum class Type(val operator: String) {
            PLUS("+"), MINUS("-");
        }

        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString(): String {
            val sb = StringBuilder()
            val prec = Precedence.of(type)
            val targetPrec = Precedence.of(target)
            sb.append(type.operator)
            if (targetPrec > prec) {
                sb.append('(').append(target).append(')')
            } else {
                sb.append(target)
            }
            return sb.toString()
        }
    }

    data class Identifier(override val section: Section, val name: String) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString() = name
    }

    data class Invocation(override val section: Section, val name: String, val arguments: List<Expr>) : Expr() {
        override fun <R> accept(visitor: Visitor<R>) = visitor.visit(this)

        override fun toString(): String {
            return arguments.joinToString(",", "$name(", ")")
        }
    }

    abstract fun <R> accept(visitor: Visitor<R>): R

    interface Visitor<R> {
        fun visit(expr: Identifier): R
        fun visit(expr: Invocation): R
        fun visit(expr: BinaryOp): R
        fun visit(expr: UnaryOp): R
        fun visit(expr: Integer): R
        fun visit(expr: Decimal): R
        fun visit(expr: Dice): R
        fun visit(expr: SolvedDice): R
    }
}