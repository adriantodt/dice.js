package com.github.adriantodt.tartar

import kotlin.math.max
import kotlin.math.min

class StringReader(s: String) {
    private var str: String = s
    private val length: Int = s.length
    private var next = 0
    private var mark = 0

    fun read(): Int {
        return if (next >= length) -1 else str[next++].toInt()
    }

    fun skip(ns: Long): Long {
        run {
            if (next >= length) return 0
            var n = min(length - next.toLong(), ns)
            n = max(-next.toLong(), n)
            next += n.toInt()
            return n
        }
    }

    fun mark(readAheadLimit: Int) {
        require(readAheadLimit >= 0) { "Read-ahead limit < 0" }
        mark = next
    }

    fun reset() {
        next = mark
    }
}