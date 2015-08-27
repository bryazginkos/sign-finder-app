package ru.kosdev.signfinder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import ru.kosdev.signfinder.transform.RotateTransformer;

/**
 * Created by brjazgin on 26.08.2015.
 */
@SpringBootApplication
@PropertySources(value = {@PropertySource("classpath:signfinder.properties")})
@ComponentScan
@Configuration
public class Application {

    @Value("${angle.correction}")
    private int correctionAngle;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "rotatePlusTransformer")
    public RotateTransformer rotatePlusTransformer() {
        return new RotateTransformer(correctionAngle);
    }

    @Bean(name = "rotateMinusTransformer")
    public RotateTransformer rotateMinusTransformer() {
        return new RotateTransformer(-correctionAngle);
    }

    @Bean(name = "upsideDownTransformer")
    public RotateTransformer upsideDownRotateTransformer() {
        return new RotateTransformer(180);
    }
}
