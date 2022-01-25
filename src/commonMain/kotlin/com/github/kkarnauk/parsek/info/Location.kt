package com.github.kkarnauk.parsek.info

/**
 * Represents a location in an input.
 */
public data class Location(
    /**
     * Number of characters before the location.
     */
    public val offset: Int,
    /**
     * Row number of the location started from 1.
     */
    public val row: Int,
    /**
     * Column number of the location started from 1.
     */
    public val column: Int
) {
    override fun toString(): String = "loc(row=$row, column=$column, offset=$offset)"
}

/**
 * Represents a default location to be used when an actual location is unknown.
 */
public val EmptyLocation: Location = Location(0, 0, 0)
