package ru.kosdev.signfinder;

import org.bytedeco.javacpp.opencv_core;


/**
 * Результат поиска места для подписи
 */
public class FindResult {

    private opencv_core.Mat transformedMat;
    private opencv_core.Rect rect;

    public FindResult(opencv_core.Mat transformedMat, opencv_core.Rect rect) {
        this.transformedMat = transformedMat;
        this.rect = rect;
    }

    /**
     * Место для подписи
     * @return
     */
    public opencv_core.Rect getRect() {
        return rect;
    }

    /**
     * Трансформированное изображение
     * @return
     */
    public opencv_core.Mat getTransformedMat() {
        return transformedMat;
    }
}
