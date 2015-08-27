package ru.kosdev.signfinder.transform;

import org.bytedeco.javacpp.opencv_core;

import java.util.function.Consumer;

/**
 * Преобразование
 */
public interface Transformer extends Consumer<opencv_core.Mat> {
}
