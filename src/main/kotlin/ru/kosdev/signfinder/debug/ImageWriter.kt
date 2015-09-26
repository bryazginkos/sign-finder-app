package ru.kosdev.signfinder.debug

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.kosdev.signfinder.FindResult
import ru.kosdev.signfinder.RectPainter

import java.io.File
import java.time.Clock

/**
 * Created by Константин on 26.09.2015.
 */

/**
 * Аспект, который после нахождения места для подписи выдяляет эту область и сохраняет ихображение в файл
 */
@Aspect
@Component
public class ImageWriter {

    @Autowired
    private lateinit val rectPainter: RectPainter;

    @Value("\${write.image}")
    private val writeImage: Boolean = false

    @Value("\${write.image.dir}")
    private val workDir: String? = null

    private val clock = Clock.systemDefaultZone()

    @Around("execution(* ru.kosdev.signfinder.Finder.findSignPlace(..))")
    @Throws(Throwable::class)
    public fun drawFound(joinPoint: ProceedingJoinPoint): FindResult? {
        val findResult = joinPoint.proceed() as FindResult?
        if (findResult != null && writeImage && workDir != null && !workDir.isEmpty()) {
            val millis = clock.millis()
            rectPainter.drawRect(findResult.transformedMat, workDir + File.separatorChar + millis + ".jpg", findResult.rect)
        }
        return findResult
    }


}