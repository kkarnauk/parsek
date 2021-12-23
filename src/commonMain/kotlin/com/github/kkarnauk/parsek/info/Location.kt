package com.github.kkarnauk.parsek.info

public class Location(
    public val offset: Int,
    public val row: Int,
    public val column: Int
) {
    override fun toString(): String = "loc(row=$row, column=$column, offset=$offset)"
}

public val EmptyLocation: Location = Location(0, 0, 0)
