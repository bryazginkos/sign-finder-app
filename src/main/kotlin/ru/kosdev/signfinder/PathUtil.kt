package ru.kosdev.signfinder

import java.io.File

/**
 * Created by Константин on 22.09.2015.
 */

public fun platformPath(string: String): String {
    return string.replace('/', File.separatorChar)
}