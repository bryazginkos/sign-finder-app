package ru.kosdev.signfinder;

import org.bytedeco.javacpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;


/**
 * Created by Константин on 21.08.2015.
 */
@Component
public class SignDetector {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Finder finder;

    @Value("${max.deviation.level}")
    private int deviationLevel;

    /**
     * Проверяет, есть ли подпись в скане.
     * @param srcFName - имя исходного файла
     * @param srcFName
     * @return
     * @throws SignPlaceNotFoundException
     */
    public boolean detectSign(String srcFName) throws SignPlaceNotFoundException {
        //читаем изображение из файла
        opencv_core.Mat mat = imread(srcFName, CV_LOAD_IMAGE_GRAYSCALE);

        //поиск места для подписи
        FindResult result = finder.findSignPlace(mat);

        if (result != null) {
            //поиск подписи
            return searchSign(result.getTransformedMat(), result.getRect());
        }
        throw new SignPlaceNotFoundException();
    }



    /**
     * Поиск подписи в фрагменте изображения
     * @param mat - матрица изображения
     * @param rect - выделенная область
     * @return
     */
    private boolean searchSign(opencv_core.Mat mat, opencv_core.Rect rect) {
        int x0 = rect.x();
        int y0 = rect.y();
        int x1 = rect.width() + x0;
        int y1 = rect.height() + y0;

        ByteBuffer byteBuffer = mat.getByteBuffer();

        //считаем среднюю "черноту" пикселя
        long blackness = 0;
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                long index = y*mat.step() + x*mat.channels();
                int color = byteBuffer.get((int)index) & 0xFF;
                blackness += (255 - color);
            }
        }

        float background = blackness/rect.width()/rect.height();

        //считаем стандартное отклонение "черноты" пикселей
        long squareDev = 0;
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                long index = y*mat.step() + x*mat.channels();
                int color = byteBuffer.get((int)index) & 0xFF;
                squareDev += (background - (255-color))*(background - (255-color));
            }
        }
        squareDev = squareDev/rect.width()/rect.height();

        return squareDev > deviationLevel*deviationLevel;
    }
}