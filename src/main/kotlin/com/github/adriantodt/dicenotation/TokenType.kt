package com.github.adriantodt.dicenotation

enum class TokenType {
    // PAIRS
    LEFT_PAREN, RIGHT_PAREN,  // TYPES
    INT, NUMBER, DICE_NOTATION,  // ARITHMETIC
    PLUS, MINUS, ASTERISK, SLASH, CARET, PERCENT,  // MISC
    SHIFT_RIGHT, SHIFT_LEFT, COMMA, DOT,  // SCRIPT
    IDENTIFIER
}