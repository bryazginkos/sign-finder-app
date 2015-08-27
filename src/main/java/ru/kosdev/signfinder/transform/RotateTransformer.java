package ru.kosdev.signfinder.transform;

import org.bytedeco.javacpp.opencv_core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bytedeco.javacpp.opencv_imgproc.getRotationMatrix2D;
import static org.bytedeco.javacpp.opencv_imgproc.warpAffine;

/**
 * Вращение на заданный угол
 */
public class RotateTransformer implements Transformer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private double angle;

    public RotateTransformer(double angle) {
        log.info("Initializing rotate transformer with " + angle + " angle");
        this.angle = angle;
    }

    @Override
    public void accept(opencv_core.Mat mat) {
        //определяем центр (ось вращения)
        opencv_core.Point2f center = new opencv_core.Point2f(mat.cols() / 2, mat.rows() / 2);
        //матрица вращения
        opencv_core.Mat rotationMatrix = getRotationMatrix2D(center, angle, 1);
        warpAffine(mat, mat, rotationMatrix, mat.size());
    }
}
