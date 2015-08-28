package ru.kosdev.signfinder.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kosdev.signfinder.SignDetector;
import ru.kosdev.signfinder.SignPlaceNotFoundException;
import ru.kosdev.signfinder.json.Result;

/**
 * Created by brjazgin on 27.08.2015.
 */
@RestController
public class Controller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SignDetector signDetector;

    @RequestMapping("/signfinder")
    public Result hasSign(@RequestParam(value="url") String url) {
        log.info("try to find sign in file: " + url);
        try {
            boolean hasSign = signDetector.detectSign(url);
            log.info("file " + url + ". Result = " + hasSign);
            return new Result(hasSign);
        } catch (SignPlaceNotFoundException e) {
            log.info("Sign place is not found in " + url);
            return null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
