package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.bytedeco.javacpp.opencv_imgproc.getRotationMatrix2D
import org.bytedeco.javacpp.opencv_imgproc.warpAffine
/**
 * Created by Константин on 27.09.2015.
 */
public class RotateTransformer(private val angle: Double) : Transformer {

    private val log = LoggerFactory.getLogger(this.javaClass)

    init {
        log.info("Initializing rotate transformer with $angle angle")
    }

    override fun accept(mat: opencv_core.Mat) {
        //определяем центр (ось вращения)
        val center = opencv_core.Point2f((mat.cols() / 2).toFloat(), (mat.rows() / 2).toFloat())
        //матрица вращения
        val rotationMatrix = getRotationMatrix2D(center, angle, 1.0)
        warpAffine(mat, mat, rotationMatrix, mat.size())
    }
}