package ru.kosdev.signfinder.debug;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kosdev.signfinder.FindResult;
import ru.kosdev.signfinder.RectPainter;

import java.io.File;
import java.time.Clock;

/**
 * Created by Константин on 27.08.2015.
 */
@Aspect
@Component
/**
 * Аспект, который после нахождения места для подписи выдяляет эту область и сохраняет ихображение в файл
 */
public class ImageWriter {

    @Autowired
    private RectPainter rectPainter;

    @Value("${write.image}")
    private boolean writeImage;

    @Value("${write.image.dir}")
    private String workDir;

    private Clock clock = Clock.systemDefaultZone();

    @Around("execution(* ru.kosdev.signfinder.Finder.findSignPlace(..))")
    public FindResult drawFound(ProceedingJoinPoint joinPoint) throws Throwable {
        FindResult findResult = (FindResult)joinPoint.proceed();
        if (findResult != null && writeImage && workDir != null && !workDir.isEmpty()) {
            long millis = clock.millis();
            rectPainter.drawRect(findResult.getTransformedMat(), workDir + File.separatorChar + millis + ".jpg", findResult.getRect());
        }
        return findResult;
    }


}
