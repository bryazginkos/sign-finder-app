package ru.kosdev.signfinder;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.kosdev.signfinder.transform.EqualizeHistTransformer;
import ru.kosdev.signfinder.transform.RotateTransformer;
import ru.kosdev.signfinder.transform.Transformer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by brjazgin on 26.08.2015.
 */
@Component
public class Finder {

    private static final String CASCADE_FILE = PathUtilKt.platformPath("src/main/resources/cascade.xml");

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EqualizeHistTransformer equalizeHistTransformer;

    @Autowired
    @Qualifier("rotateMinusTransformer")
    private RotateTransformer rotateMinusTransformer;

    @Autowired
    @Qualifier("rotatePlusTransformer")
    private RotateTransformer rotatePlusTransformer;

    @Autowired
    @Qualifier("upsideDownTransformer")
    private RotateTransformer upsideDownTransformer;

    private opencv_objdetect.CascadeClassifier classifier;

    public Finder() {
        Path path = Paths.get(CASCADE_FILE);
        String absolutePath = path.toAbsolutePath().toString();
        log.info("Load cascade " + absolutePath);
        classifier = new opencv_objdetect.CascadeClassifier(absolutePath);
    }

    /**
     * Осуществляет поиск места для подписи, выполняя различные преобразования над изображением
     * @param mat
     * @return
     */
    public FindResult findSignPlace(opencv_core.Mat mat) {
        FindResult result;

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
    private FindResult zeroLevelFind(opencv_core.Mat mat) {
        return find(mat, Collections.EMPTY_LIST);
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 1.
     * @param mat
     * @return
     */
    private FindResult firstLevelFind(opencv_core.Mat mat) {
        FindResult findResult;

        findResult = find(mat, Arrays.asList(equalizeHistTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(rotateMinusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(rotatePlusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(upsideDownTransformer));
        if (findResult != null) return findResult;

        return null;
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 2.
     * @param mat
     * @return
     */
    private FindResult secondLevelFind(opencv_core.Mat mat) {
        FindResult findResult;

        findResult = find(mat, Arrays.asList(upsideDownTransformer, equalizeHistTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(upsideDownTransformer, rotateMinusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(upsideDownTransformer, rotatePlusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(equalizeHistTransformer, rotateMinusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(equalizeHistTransformer, rotatePlusTransformer));
        if (findResult != null) return findResult;

        return null;
    }

    /**
     * Поиск места для подписи, пытаясь трансформировать изображение различными преобразованиями.
     * Число преобразований в попытке 3.
     * @param mat
     * @return
     */
    private FindResult thirdLevelFind(opencv_core.Mat mat) {
        FindResult findResult;

        findResult = find(mat, Arrays.asList(equalizeHistTransformer, upsideDownTransformer, rotateMinusTransformer));
        if (findResult != null) return findResult;

        findResult = find(mat, Arrays.asList(equalizeHistTransformer, upsideDownTransformer, rotatePlusTransformer));
        if (findResult != null) return findResult;

        return null;
    }

    /**
     * Выполняет над изображением преобразования и осуществляет поиск места для подписи
     * @param mat - изображение
     * @param functions - преобразования
     * @return
     */
    private FindResult find(opencv_core.Mat mat, List<Transformer> functions) {
        final opencv_core.Mat transformed = mat.clone();
        functions.forEach(transformer -> transformer.accept(transformed));
        opencv_core.RectVector rectVector = new opencv_core.RectVector();
        classifier.detectMultiScale(transformed, rectVector);
        if (rectVector.size() > 0) {
            return new FindResult(transformed, reduceTwice(getMaxRect(rectVector)));
        } else {
            return null;
        }
    }

    /**
     * Возвразает максимальный прямоугольник
     * @param rectVector
     * @return
     */
    private opencv_core.Rect getMaxRect(opencv_core.RectVector rectVector) {
        int maxWidth = 0;
        opencv_core.Rect result = null;
        for (int i = 0; i <= rectVector.size(); i++) {
            opencv_core.Rect currentRect = rectVector.get(i);
            int width = currentRect.width();
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
    private opencv_core.Rect reduceTwice(opencv_core.Rect big) {
        int height = big.height();
        int width = big.width();
        int x = big.tl().x();
        int y = big.tl().y();
        return new opencv_core.Rect(x+width/4, y+height/4, width/2, height/2);
    }
}
