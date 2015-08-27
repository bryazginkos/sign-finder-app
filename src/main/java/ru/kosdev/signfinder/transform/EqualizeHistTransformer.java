package ru.kosdev.signfinder.transform;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.springframework.stereotype.Component;

/**
 * Выравнивание гистограммы
 * http://docs.opencv.org/doc/tutorials/imgproc/histograms/histogram_equalization/histogram_equalization.html
 */
@Component
public class EqualizeHistTransformer implements Transformer {

    @Override
    public void accept(opencv_core.Mat mat) {
        opencv_imgproc.equalizeHist(mat, mat);
    }

}
