package ru.kosdev.signfinder

import org.bytedeco.javacpp.opencv_core

/**
 * Created by Константин on 26.09.2015.
 */
public data class FindResult (transformedMat :opencv_core.Mat, rect : opencv_core.Rect) {
    val transformedMat = transformedMat
    val rect = rect
}