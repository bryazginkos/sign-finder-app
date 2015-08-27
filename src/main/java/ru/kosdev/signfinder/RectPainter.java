package ru.kosdev.signfinder;

import org.bytedeco.javacpp.opencv_core;
import org.springframework.stereotype.Component;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

/**
 * Created by brjazgin on 26.08.2015.
 */
@Component
public class RectPainter {

    /**
     * Рисует прямоугольник и сохраняет в файл
     * @param mat - исходная матрица
     * @param resultFName - имя файла
     * @param rect - прямоугольник
     */
    public void drawRect(opencv_core.Mat mat, String resultFName, opencv_core.Rect rect) {
        opencv_core.Mat paintMat = mat.clone();
        int height = rect.height();
        int width = rect.width();
        int x = rect.tl().x();
        int y = rect.tl().y();
        opencv_core.Point start = new opencv_core.Point(x, y);
        opencv_core.Point finish = new opencv_core.Point(x+width, y + height);
        rectangle(paintMat, start, finish, opencv_core.Scalar.all(0));
        imwrite(resultFName, paintMat);
    }
}
