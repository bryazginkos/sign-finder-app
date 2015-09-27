package ru.kosdev.signfinder

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.*
import ru.kosdev.signfinder.transform.RotateTransformProvider

/**
 * Created by Константин on 26.09.2015.
 */
@SpringBootApplication
@PropertySources(value = *arrayOf(PropertySource("classpath:signfinder.properties")))
@ComponentScan
@Configuration
public open class Application {

    @Value("\${angle.correction}")
    private val correctionAngle: Int = 0

    @Bean(name = arrayOf("rotatePlusTransformer"))
    public open fun rotatePlusTransformer(): RotateTransformProvider {
        return RotateTransformProvider(correctionAngle.toDouble())
    }

    @Bean(name = arrayOf("rotateMinusTransformer"))
    public open fun rotateMinusTransformer(): RotateTransformProvider {
        return RotateTransformProvider((-correctionAngle).toDouble())
    }

    @Bean(name = arrayOf("upsideDownTransformer"))
    public open fun upsideDownRotateTransformer(): RotateTransformProvider {
        return RotateTransformProvider(180.0)
    }

    companion object {
        @JvmStatic
        public fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
}