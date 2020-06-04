package com.github.adriantodt.dicenotation

import com.github.adriantodt.tartar.api.parser.InfixParser
import com.github.adriantodt.tartar.api.parser.ParserContext
import com.github.adriantodt.tartar.api.parser.Token

class ParserBinaryOp(
    override val precedence: Int, private val leftAssoc: Boolean, val type: Expr.BinaryOp.Type
) : InfixParser<TokenType, Expr> {
    override fun parse(ctx: ParserContext<TokenType, Expr>, left: Expr, token: Token<TokenType>): Expr {
        val right = ctx.parseExpression(precedence - if (leftAssoc) 0 else 1)
        return Expr.BinaryOp(token.section, left, right, type)
    }
}