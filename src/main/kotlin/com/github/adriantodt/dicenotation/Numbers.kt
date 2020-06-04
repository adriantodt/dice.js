package com.github.adriantodt.dicenotation

import kotlin.math.pow

object Numbers {
    fun divide(left: Number, right: Number): Number {
        if (left is Double || right is Double) return left.toDouble() / right.toDouble()
        if (left is Float || right is Float) return left.toFloat() / right.toFloat()
        return if (left is Long || right is Long) left.toLong() / right.toFloat() else left.toInt() / right.toInt()
    }
    
    fun leftShift(left: Number, right: Number): Number {
        val shift: Long = left.toLong() shl right.toInt()
        return if (left is Int && right is Int) shift.toInt() else shift
    }
    
    fun minus(left: Number, right: Number): Number {
        if (left is Double || right is Double) return left.toDouble() - right.toDouble()
        if (left is Float || right is Float) return left.toFloat() - right.toFloat()
        return if (left is Long || right is Long) left.toLong() - right.toFloat() else left.toInt() - right.toInt()
    }

    fun modulus(left: Number, right: Number): Number {
        if (left is Double || right is Double) return left.toDouble() % right.toDouble()
        if (left is Float || right is Float) return left.toFloat() % right.toFloat()
        return if (left is Long || right is Long) left.toLong() % right.toFloat() else left.toInt() / right.toInt()
    }
    
    fun plus(left: Number, right: Number): Number {
        if (left is Double || right is Double) return left.toDouble() + right.toDouble()
        if (left is Float || right is Float) return left.toFloat() + right.toFloat()
        return if (left is Long || right is Long) left.toLong() + right.toFloat() else left.toInt() + right.toInt()
    }
    
    fun power(left: Number, right: Number): Number {
        val pow = left.toDouble().pow(right.toDouble())
        if (left is Double || right is Double) return pow
        if (left is Float || right is Float) return pow.toFloat()
        if (pow.toInt().toDouble() == pow) return pow.toInt()
        return if (pow.toLong().toDouble() == pow) pow.toLong() else pow
    }

    fun rightShift(left: Number, right: Number): Number {
        val shift: Long = left.toLong() shr right.toInt()
        return if (left is Int && right is Int) shift.toInt() else shift
    }
    
    fun times(left: Number, right: Number): Number {
        if (left is Double || right is Double) return left.toDouble() * right.toDouble()
        if (left is Float || right is Float) return left.toFloat() * right.toFloat()
        return if (left is Long || right is Long) left.toLong() * right.toFloat() else left.toInt() * right.toInt()
    }

    fun unaryMinus(target: Number): Number {
        if (target is Double) return -target.toDouble()
        if (target is Float) return -target.toFloat()
        return if (target is Long) -target.toLong() else -target.toInt()
    }

    fun unaryPlus(target: Number): Number {
        if (target is Double) return +target.toDouble()
        if (target is Float) return +target.toFloat()
        return if (target is Long) +target.toLong() else +target.toInt()
    }
}