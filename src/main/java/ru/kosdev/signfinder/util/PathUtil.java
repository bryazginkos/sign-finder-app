package ru.kosdev.signfinder.util;

import java.io.File;

/**
 * Created by brjazgin on 27.08.2015.
 */
public class PathUtil {
    public static String platformPath(String string) {
        return string.replace('/', File.separatorChar);
    }
}
