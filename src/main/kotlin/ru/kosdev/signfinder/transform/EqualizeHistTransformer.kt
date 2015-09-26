package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core
import org.bytedeco.javacpp.opencv_imgproc
import org.springframework.stereotype.Component
/**
 * Created by Константин on 27.09.2015.
 */
@Component
public class EqualizeHistTransformer : Transformer {

    override fun accept(mat: opencv_core.Mat) {
        opencv_imgproc.equalizeHist(mat, mat)
    }

}