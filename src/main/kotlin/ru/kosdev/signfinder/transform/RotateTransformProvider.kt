package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core
import org.slf4j.LoggerFactory

import org.bytedeco.javacpp.opencv_imgproc.getRotationMatrix2D
import org.bytedeco.javacpp.opencv_imgproc.warpAffine
/**
 * Created by Константин on 27.09.2015.
 */
public class RotateTransformProvider(private val angle: Double) : TransformProvider {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val transformFunction: (opencv_core.Mat) -> opencv_core.Mat = { mat -> transform(mat) }

    init {
        log.info("Initializing rotate transformer with $angle angle")
    }

    override fun getTransformFunction(): (opencv_core.Mat) -> opencv_core.Mat {
        return transformFunction
    }

    private fun transform(mat: opencv_core.Mat) : opencv_core.Mat {
        //определяем центр (ось вращения)
        val center = opencv_core.Point2f((mat.cols() / 2).toFloat(), (mat.rows() / 2).toFloat())
        //матрица вращения
        val rotationMatrix = getRotationMatrix2D(center, angle, 1.0)
        warpAffine(mat, mat, rotationMatrix, mat.size())
        return mat
    }
}