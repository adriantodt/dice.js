package com.github.adriantodt.dicenotation.evaluator

import com.github.adriantodt.dicenotation.Expr

class DiceSolver(private val random: (Int, Int) -> Int) : Expr.Visitor<Expr> {
    constructor(random: (Int) -> Int) : this({ sides: Int, _: Int -> random(sides) })

    override fun visit(expr: Expr.Invocation): Expr = expr.copy(arguments = expr.arguments.map(this::apply))
    override fun visit(expr: Expr.BinaryOp): Expr = expr.copy(
        left = expr.left.accept(this),
        right = expr.right.accept(this)
    )
    override fun visit(expr: Expr.UnaryOp): Expr = expr.copy(target = expr.target.accept(this))
    override fun visit(expr: Expr.Dice): Expr = Expr.SolvedDice(expr, List(expr.rolls) { random(expr.sides, it) })
    override fun visit(expr: Expr.Identifier) = expr
    override fun visit(expr: Expr.Integer) = expr
    override fun visit(expr: Expr.Decimal) = expr
    override fun visit(expr: Expr.SolvedDice) = expr

    private fun apply(expr: Expr) = expr.accept(this)
}