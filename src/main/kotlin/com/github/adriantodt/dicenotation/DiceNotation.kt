package com.github.adriantodt.dicenotation

import com.github.adriantodt.tartar.api.parser.SyntaxException
import com.github.adriantodt.tartar.api.parser.Token
import com.github.adriantodt.tartar.createGrammar
import com.github.adriantodt.tartar.createLexer
import com.github.adriantodt.tartar.createParser
import com.github.adriantodt.tartar.extensions.*

val lexer = createLexer<Token<TokenType>> {
    ' '()
    '\t'()
    '\r'()
    '\n'()
    '(' { process(makeToken(TokenType.LEFT_PAREN)) }
    ')' { process(makeToken(TokenType.RIGHT_PAREN)) }
    '.' { process(makeToken(TokenType.DOT)) }
    ',' { process(makeToken(TokenType.COMMA)) }
    '+' { process(makeToken(TokenType.PLUS)) }
    '-' { process(makeToken(TokenType.MINUS)) }
    '*' { process(makeToken(TokenType.ASTERISK)) }
    '/' { process(makeToken(TokenType.SLASH)) }
    '%' { process(makeToken(TokenType.PERCENT)) }
    '^' { process(makeToken(TokenType.CARET)) }
    ">>" { process(makeToken(TokenType.SHIFT_RIGHT)) }
    "<<" { process(makeToken(TokenType.SHIFT_LEFT)) }
    matching { it.isDigit() }.configure {
        process(
            when (val n = readNumber(it)) {
                is LexicalNumber.Invalid -> when {
                    n.string.matches("^\\d*[Dd]\\d+") -> makeToken(TokenType.DICE_NOTATION, n.string)
                    else -> throw SyntaxException("Invalid number '${n.string}'", section(n.string.length))
                }
                is LexicalNumber.Decimal -> makeToken(TokenType.NUMBER, n.value.toString())
                is LexicalNumber.Integer -> makeToken(TokenType.INT, n.value.toString())
            }
        )
    }
    matching { it.isLetter() || it == '_' }.configure {
        val s = readIdentifier(it)
        process(
            when {
                s.matches("^\\d*[Dd]\\d+") -> makeToken(TokenType.DICE_NOTATION, s)
                else -> makeToken(TokenType.IDENTIFIER, s)
            }
        )
    }
}

val grammar = createGrammar<TokenType, Expr> {
    // BLOCKS
    prefix(TokenType.LEFT_PAREN) { parseExpression().also { eat(TokenType.RIGHT_PAREN) } }

    // NODES
    prefix(TokenType.INT) { Expr.Integer(it.section, it.value.toInt()) }
    prefix(TokenType.NUMBER) { Expr.Decimal(it.section, it.value.toDouble()) }
    prefix(TokenType.DICE_NOTATION) {
        val s = it.value.toLowerCase()
        if (s[0] == 'd') {
            Expr.Dice(it.section, 1, s.substring(1).toInt())
        } else {
            val i = s.indexOf('d')
            if (i == -1) {
                throw SyntaxException("Invalid dice notation `$s`", it.section)
            }
            Expr.Dice(it.section, s.substring(0, i).toInt(), s.substring(i + 1).toInt())
        }
    }
    prefix(TokenType.IDENTIFIER) {
        when {
            nextIsAny(TokenType.INT, TokenType.NUMBER, TokenType.DICE_NOTATION, TokenType.IDENTIFIER) -> {
                Expr.Invocation(it.section, it.value, listOf(parseExpression()))
            }
            nextIs(TokenType.LEFT_PAREN) -> {
                val args = mutableListOf<Expr>()
                eat(TokenType.LEFT_PAREN)
                if (!nextIs(TokenType.RIGHT_PAREN)) while (true) {
                    args.add(parseExpression())
                    if (nextIs(TokenType.RIGHT_PAREN)) break
                    eat(TokenType.COMMA)
                }
                eat(TokenType.RIGHT_PAREN)
                Expr.Invocation(it.section, it.value, args)
            }
            else -> {
                Expr.Identifier(it.section, it.value)
            }
        }
    }

    // Numeric
    prefix(TokenType.MINUS, ParserUnaryOp(Expr.UnaryOp.Type.MINUS))
    prefix(TokenType.PLUS, ParserUnaryOp(Expr.UnaryOp.Type.PLUS))
    infix(TokenType.PLUS, ParserBinaryOp(Precedence.ADDITIVE, true, Expr.BinaryOp.Type.PLUS))
    infix(TokenType.MINUS, ParserBinaryOp(Precedence.ADDITIVE, true, Expr.BinaryOp.Type.MINUS))
    infix(TokenType.ASTERISK, ParserBinaryOp(Precedence.MULTIPLICATIVE, true, Expr.BinaryOp.Type.TIMES))
    infix(TokenType.SLASH, ParserBinaryOp(Precedence.MULTIPLICATIVE, true, Expr.BinaryOp.Type.DIVIDE))
    infix(TokenType.PERCENT, ParserBinaryOp(Precedence.MULTIPLICATIVE, true, Expr.BinaryOp.Type.MODULUS))
    infix(TokenType.CARET, ParserBinaryOp(Precedence.EXPONENTIAL, false, Expr.BinaryOp.Type.POWER))
    infix(TokenType.SHIFT_RIGHT, ParserBinaryOp(Precedence.SHIFT, true, Expr.BinaryOp.Type.SHR))
    infix(TokenType.SHIFT_LEFT, ParserBinaryOp(Precedence.SHIFT, true, Expr.BinaryOp.Type.SHL))
}

val parser = createParser(grammar) {
    ensureEOF {
        mutableListOf<Expr>().also { list ->
            while (true) {
                list.add(parseExpression())
                if (eof) break
                eat(TokenType.COMMA)
            }
        }
    }
}
