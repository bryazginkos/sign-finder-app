package ru.kosdev.signfinder

import org.bytedeco.javacpp.opencv_core
import org.springframework.stereotype.Component

import org.bytedeco.javacpp.opencv_imgcodecs.imwrite
import org.bytedeco.javacpp.opencv_imgproc.rectangle

/**
 * Created by Константин on 26.09.2015.
 */
@Component
public class RectPainter {

    /**
     * Рисует прямоугольник и сохраняет в файл
     * @param mat - исходная матрица
     * @param resultFName - имя файла
     * @param rect - прямоугольник
     */
    public fun drawRect(mat: opencv_core.Mat, resultFName: String, rect: opencv_core.Rect) {
        val paintMat = mat.clone()
        val height = rect.height()
        val width = rect.width()
        val x = rect.tl().x()
        val y = rect.tl().y()
        val start = opencv_core.Point(x, y)
        val finish = opencv_core.Point(x + width, y + height)
        rectangle(paintMat, start, finish, opencv_core.Scalar.all(0.0))
        imwrite(resultFName, paintMat)
    }
}
