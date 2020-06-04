package com.github.adriantodt.dicenotation

object Precedence {
    /* a + b | a - b */
    const val ADDITIVE = 8

    /* a ^ b */
    const val EXPONENTIAL = 11

    /* a * b | a / b | a % b */
    const val MULTIPLICATIVE = 9

    /* a(b) */
    const val POSTFIX = 13

    /* -a | +a | !a | ~a */
    const val PREFIX = 10

    /* Int, Decimals, Dice Notation */
    const val PURE = 6
    const val SHIFT = 7

    fun of(op: Expr.UnaryOp.Type): Int {
        return PREFIX
    }

    fun of(op: Expr.BinaryOp.Type): Int {
        return when (op) {
            Expr.BinaryOp.Type.PLUS, Expr.BinaryOp.Type.MINUS -> ADDITIVE
            Expr.BinaryOp.Type.TIMES, Expr.BinaryOp.Type.DIVIDE, Expr.BinaryOp.Type.MODULUS -> MULTIPLICATIVE
            Expr.BinaryOp.Type.POWER -> EXPONENTIAL
            Expr.BinaryOp.Type.SHL, Expr.BinaryOp.Type.SHR -> SHIFT
        }
    }

    fun of(expr: Expr): Int {
        return when (expr) {
            is Expr.Decimal, is Expr.Dice, is Expr.SolvedDice, is Expr.Integer, is Expr.Identifier -> PURE
            is Expr.Invocation -> POSTFIX
            is Expr.BinaryOp -> of(expr.type)
            is Expr.UnaryOp -> PREFIX
        }
    }
}