package ru.kosdev.signfinder.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.kosdev.signfinder.SignDetector
import ru.kosdev.signfinder.SignPlaceNotFoundException
import ru.kosdev.signfinder.json.Result

/**
 * Created by Константин on 22.09.2015.
 */
@RestController
public class Controller {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit val signDetector: SignDetector

    @RequestMapping("/signfinder")
    public fun hasSign(@RequestParam(value = "url") url: String): Result {
        log.info("try to find sign in file: " + url)
        try {
            val hasSign = signDetector.detectSign(url)
            log.info("file $url. Result = ${Result(hasSign)}")
            return Result(hasSign)
        } catch (e: SignPlaceNotFoundException) {
            log.info("Sign place is not found in $url")
            return Result("Sing place is not found")
        } catch (e: Exception) {
            log.error(e.getMessage(), e)
            return Result(e.getMessage().toString())
        }
    }
}