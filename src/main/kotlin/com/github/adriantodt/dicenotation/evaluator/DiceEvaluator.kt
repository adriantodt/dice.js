package com.github.adriantodt.dicenotation.evaluator

import com.github.adriantodt.dicenotation.Expr
import com.github.adriantodt.dicenotation.Numbers.divide
import com.github.adriantodt.dicenotation.Numbers.leftShift
import com.github.adriantodt.dicenotation.Numbers.minus
import com.github.adriantodt.dicenotation.Numbers.modulus
import com.github.adriantodt.dicenotation.Numbers.plus
import com.github.adriantodt.dicenotation.Numbers.power
import com.github.adriantodt.dicenotation.Numbers.rightShift
import com.github.adriantodt.dicenotation.Numbers.times
import com.github.adriantodt.dicenotation.Numbers.unaryMinus
import com.github.adriantodt.dicenotation.Numbers.unaryPlus

class DiceEvaluator : Expr.Visitor<Number> {
    private val values = mutableMapOf<String, DValue>()
    private val functions = mutableMapOf<String, DFunction>()

    fun function(name: String, function: DFunction) = apply {
        functions[name] = function
    }

    fun functionAlias(name: String, vararg alias: String) = apply {
        val f = functions[name]!!
        for (k in alias) functions[k] = f
    }

    fun value(name: String, value: Number) = apply {
        values[name] = { value }
    }

    fun value(name: String, value: DValue) = apply {
        values[name] = value
    }

    fun valueAlias(name: String, vararg alias: String) = apply {
        val v = values[name]!!
        for (k in alias) values[k] = v
    }

    override fun visit(expr: Expr.Identifier): Number {
        val v = values[expr.name] ?: throw Exception("Constant `" + expr.name + "` doesn't exist.")
        return v()
    }

    override fun visit(expr: Expr.Invocation): Number {
        val f = functions[expr.name] ?: throw Exception("Function `" + expr.name + "` doesn't exist.")
        val args = expr.arguments.map(this::apply).toTypedArray()
        return f(args)
    }

    override fun visit(expr: Expr.BinaryOp): Number {
        val left = expr.left.accept(this)
        val right = expr.right.accept(this)
        return when (expr.type) {
            Expr.BinaryOp.Type.PLUS -> plus(left, right)
            Expr.BinaryOp.Type.MINUS -> minus(left, right)
            Expr.BinaryOp.Type.TIMES -> times(left, right)
            Expr.BinaryOp.Type.DIVIDE -> divide(left, right)
            Expr.BinaryOp.Type.MODULUS -> modulus(left, right)
            Expr.BinaryOp.Type.POWER -> power(left, right)
            Expr.BinaryOp.Type.SHL -> leftShift(left, right)
            Expr.BinaryOp.Type.SHR -> rightShift(left, right)
        }
    }

    override fun visit(expr: Expr.UnaryOp): Number {
        val target = expr.target.accept(this)
        return when (expr.type) {
            Expr.UnaryOp.Type.PLUS -> unaryPlus(target)
            Expr.UnaryOp.Type.MINUS -> unaryMinus(target)
        }
    }

    override fun visit(expr: Expr.Integer) = expr.value
    override fun visit(expr: Expr.Decimal) = expr.value
    override fun visit(expr: Expr.Dice) = throw Exception("Dice wasn't solved.")
    override fun visit(expr: Expr.SolvedDice) = expr.result

    private fun apply(expr: Expr) = expr.accept(this)
}