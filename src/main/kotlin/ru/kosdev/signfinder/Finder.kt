package ru.kosdev.signfinder;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.kosdev.signfinder.transform.EqualizeHistTransformer;
import ru.kosdev.signfinder.transform.RotateTransformer;
import ru.kosdev.signfinder.transform.Transformer;

import java.nio.file.Paths;

/**
 * Created by brjazgin on 26.08.2015.
 */
@Component
open public class Finder {

    private val CASCADE_FILE_NAME =  platformPath("src/main/resources/cascade.xml")
    private val logger = LoggerFactory.getLogger(this.javaClass);

    @Autowired
    private lateinit val equalizeHistTransformer : EqualizeHistTransformer

    @Autowired
    @Qualifier("rotateMinusTransformer")
    private lateinit val rotateMinusTransformer : RotateTransformer

    @Autowired
    @Qualifier("rotatePlusTransformer")
    private lateinit val rotatePlusTransformer : RotateTransformer

    @Autowired
    @Qualifier("upsideDownTransformer")
    private lateinit val upsideDownTransformer : RotateTransformer

    private val classifier : opencv_objdetect.CascadeClassifier

    init {
        var path = Paths.get(CASCADE_FILE_NAME);
        var absolutePath = path.toAbsolutePath().toString();
        logger.info("Load cascade " + absolutePath);
        classifier = opencv_objdetect.CascadeClassifier(absolutePath);
    }

    /**
     * Осуществляет поиск места для подписи, выполняя различные преобразования над изображением
     * @param mat
     * @return
     */
    public open fun findSignPlace(mat : opencv_core.Mat) : FindResult? {
        var result :FindResult?

        result = zeroLevelFind(mat);
        if (result != null) return result;

        result = firstLevelFind(mat);
        if (result != null) return result;

        result = secondLevelFind(mat);
        if (result != null) return result;

        result = thirdLevelFind(mat);
        if (result != null) return result;

        return null;
    }

    /**
     * Поиск места для подписи без преобразований над изображением
     * @param mat
     * @return
     */
    private fun zeroLevelFind(mat : opencv_core.Mat) : FindResult? {
        return find(mat, listOf())
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 1.
     * @param mat
     * @return
     */
    private fun firstLevelFind(mat : opencv_core.Mat) : FindResult? {
        var findResult : FindResult?;

        findResult = find(mat, listOfNotNull(equalizeHistTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(rotateMinusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(rotatePlusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(upsideDownTransformer))
        if (findResult != null) return findResult

        return null;
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 2.
     * @param mat
     * @return
     */
    private fun secondLevelFind(mat :opencv_core.Mat) : FindResult? {
        var findResult : FindResult?;

        findResult = find(mat, listOfNotNull(upsideDownTransformer, equalizeHistTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(upsideDownTransformer, rotateMinusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(upsideDownTransformer, rotatePlusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(equalizeHistTransformer, rotateMinusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(equalizeHistTransformer, rotatePlusTransformer))
        if (findResult != null) return findResult

        return null
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 3.
     * @param mat
     * @return
     */
    private fun thirdLevelFind(mat : opencv_core.Mat) : FindResult? {
        var findResult : FindResult?;

        findResult = find(mat, listOfNotNull(equalizeHistTransformer, upsideDownTransformer, rotateMinusTransformer))
        if (findResult != null) return findResult

        findResult = find(mat, listOfNotNull(equalizeHistTransformer, upsideDownTransformer, rotatePlusTransformer))
        if (findResult != null) return findResult

        return null
    }

    /**
     * Выполняет над изображением преобразования и осуществляет поиск места для подписи
     * @param mat - изображение
     * @param functions - преобразования
     * @return
     */
    private fun find(mat :opencv_core.Mat, functions : kotlin.List<Transformer>) : FindResult? {
        var transformedMat = mat.clone()
        functions.forEach { it accept(transformedMat) }
        var rectVector = opencv_core.RectVector()
        classifier.detectMultiScale(transformedMat, rectVector)
        if (rectVector.size() > 0) {
            return FindResult(transformedMat, reduceTwice(getMaxRect(rectVector)))
        } else {
            return null;
        }
    }

    /**
     * Возвразает максимальный прямоугольник
     * @param rectVector
     * @return
     */
    private fun getMaxRect(rectVector : opencv_core.RectVector) : opencv_core.Rect {
        var maxWidth = 0;
        var result : opencv_core.Rect = rectVector.get(0);
        for (i in 0 .. rectVector.size() - 1) {
            var currentRect = rectVector.get(i);
            var width = currentRect.width();
            if (width > maxWidth) {
                maxWidth = width;
                result = currentRect;
            }
        }
        return result;
    }

    /**
     * Уменьшает размер прямоугольника в два раза, сохраняя положение центра
     * @param big
     * @return
     */
    private fun reduceTwice(big :opencv_core.Rect) : opencv_core.Rect {
        var height = big.height();
        var width = big.width();
        var x = big.tl().x();
        var y = big.tl().y();
        return opencv_core.Rect(x+width/4, y+height/4, width/2, height/2);
    }
}
