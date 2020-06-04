package com.github.adriantodt.dicenotation

import com.github.adriantodt.tartar.api.parser.ParserContext
import com.github.adriantodt.tartar.api.parser.PrefixParser
import com.github.adriantodt.tartar.api.parser.Token

class ParserUnaryOp(val type: Expr.UnaryOp.Type) : PrefixParser<TokenType, Expr> {
    override fun parse(parser: ParserContext<TokenType, Expr>, token: Token<TokenType>): Expr {
        return Expr.UnaryOp(token.section, parser.parseExpression(Precedence.PREFIX), type)
    }
}