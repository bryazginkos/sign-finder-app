package ru.kosdev.signfinder;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.kosdev.signfinder.util.PathUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brjazgin on 27.08.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
public class SamplesTest {

    private static final String SAMPLES_DIR = "src/test/resources/images";

    private static final int IMAGES_NUMBER = 92;

    private static final List<Integer> NOT_SIGN_LIST = Arrays.asList(12, 18, 22, 24, 27, 28, 33, 39, 79);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private SignDetector signDetector;

    @Ignore //no samples
    @Test
    public void testSamples() throws SignPlaceNotFoundException {
        List<Integer> notFoundIds = new ArrayList<>();
        for (int i = 1; i <= IMAGES_NUMBER; i++) {
            String path = PathUtil.platformPath(SAMPLES_DIR + "/" + i + ".jpg");
            boolean hasSign = signDetector.detectSign(Paths.get(path).toAbsolutePath().toString());
            if (!hasSign) {
                notFoundIds.add(i);
            }
        }
        Assert.assertEquals(NOT_SIGN_LIST, notFoundIds);
    }

    @Ignore //no samples
    @Test
    public void testSignNotFoundException() throws SignPlaceNotFoundException {
        expectedException.expect(SignPlaceNotFoundException.class);

        String path = PathUtil.platformPath(SAMPLES_DIR + "/no-place.jpg");
        signDetector.detectSign(Paths.get(path).toAbsolutePath().toString());
    }
}
