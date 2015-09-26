package ru.kosdev.signfinder

import org.bytedeco.javacpp.opencv_core
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE
import org.bytedeco.javacpp.opencv_imgcodecs.imread

/**
 * Created by Константин on 27.09.2015.
 */
@Component
public class SignDetector {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit val finder: Finder

    @Value("\${max.deviation.level}")
    private val deviationLevel: Int = 0

    /**
     * Проверяет, есть ли подпись в скане.
     * @param srcFName - имя исходного файла
     * *
     * @param srcFName
     * *
     * @return
     * *
     * @throws SignPlaceNotFoundException
     */
    @Throws(SignPlaceNotFoundException::class)
    public fun detectSign(srcFName: String): Boolean {
        //читаем изображение из файла
        val mat = imread(srcFName, CV_LOAD_IMAGE_GRAYSCALE)

        //поиск места для подписи
        val result = finder.findSignPlace(mat)

        if (result != null) {
            //поиск подписи
            return searchSign(result.transformedMat, result.rect)
        }
        throw SignPlaceNotFoundException()
    }


    /**
     * Поиск подписи в фрагменте изображения
     * @param mat - матрица изображения
     * *
     * @param rect - выделенная область
     * *
     * @return
     */
    private fun searchSign(mat: opencv_core.Mat, rect: opencv_core.Rect): Boolean {
        val x0 = rect.x()
        val y0 = rect.y()
        val x1 = rect.width() + x0
        val y1 = rect.height() + y0

        val byteBuffer = mat.byteBuffer

        //считаем среднюю "черноту" пикселя
        var blackness: Long = 0
        for (y in y0..y1) {
            for (x in x0..x1) {
                val index = y * mat.step() + x * mat.channels()
                val byteColor = byteBuffer.get(index.toInt())
                val color = byteColor.toInt() and 255
                blackness += (255 - color).toLong()
            }
        }

        val background = blackness / rect.width().toLong() / rect.height().toLong().toFloat()

        //считаем стандартное отклонение "черноты" пикселей
        var squareDev: Long = 0
        for (y in y0..y1) {
            for (x in x0..x1) {
                val index = y * mat.step() + x * mat.channels()
                val color = byteBuffer.get(index.toInt()).toInt() and 255
                squareDev += ((background - (255 - color)) * (background - (255 - color))).toLong()
            }
        }
        squareDev = squareDev / rect.width().toLong() / rect.height().toLong()

        return squareDev > deviationLevel * deviationLevel
    }
}