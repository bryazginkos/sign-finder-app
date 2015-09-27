package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core

/**
 * Created by Константин on 27.09.2015.
 */
public interface TransformProvider {
    fun getTransformFunction() : (val mat: opencv_core.Mat) -> opencv_core.Mat
}