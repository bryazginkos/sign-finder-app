package ru.kosdev.signfinder.json

/**
 * Created by Константин on 26.09.2015.
 */
public data class Result private constructor (val result : Boolean?, val error : String?) {
    constructor(result: Boolean) : this(result, null)
    constructor(error: String) : this(null, error)
}