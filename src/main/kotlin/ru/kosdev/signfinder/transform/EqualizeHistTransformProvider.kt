package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core
import org.bytedeco.javacpp.opencv_imgproc
import org.springframework.stereotype.Component

/**
 * Created by Константин on 27.09.2015.
 */
@Component
public class EqualizeHistTransformProvider : TransformProvider {

    private val transformFunction: (opencv_core.Mat) -> opencv_core.Mat = { mat -> transform(mat) }

    override fun getTransformFunction(): (opencv_core.Mat) -> opencv_core.Mat {
        return transformFunction
    }

    private fun transform(mat : opencv_core.Mat) : opencv_core.Mat {
        opencv_imgproc.equalizeHist(mat, mat)
        return mat
    }
}