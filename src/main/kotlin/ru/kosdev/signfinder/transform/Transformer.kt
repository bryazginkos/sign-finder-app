package ru.kosdev.signfinder.transform

import org.bytedeco.javacpp.opencv_core
import java.util.function.Consumer

/**
 * Created by Константин on 27.09.2015.
 */
public interface Transformer : Consumer<opencv_core.Mat>