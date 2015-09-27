package ru.kosdev.signfinder

import org.junit.Assert
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import java.nio.file.Paths
import java.util.ArrayList
import java.util.Arrays
/**
 * Created by Константин on 27.09.2015.
 */
@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(Application::class ))
@TestPropertySource(locations = arrayOf("classpath:test.properties"))
public class SamplesTest {

    private val SAMPLES_DIR = "src/test/resources/images"

    private val IMAGES_NUMBER = 92

    private val NOT_SIGN_LIST = Arrays.asList(12, 18, 22, 24, 27, 28, 33, 39, 79)

    @Autowired
    private lateinit val signDetector: SignDetector

    private var expectedException: ExpectedException = ExpectedException.none()

    @Rule
    public fun getTestRule() : ExpectedException = expectedException //kotlin bag

    @Ignore //no samples
    @Test
    public fun testSamples() {
        val notFoundIds = ArrayList<Int>()
        for (i in 1..IMAGES_NUMBER) {
            val path = platformPath("$SAMPLES_DIR/$i.jpg")
            val hasSign = signDetector.detectSign(Paths.get(path).toAbsolutePath().toString())
            if (!hasSign) {
                notFoundIds.add(i)
            }
        }
        Assert.assertEquals(NOT_SIGN_LIST, notFoundIds)
    }

    @Ignore //no samples
    @Test
    public fun testSignNotFoundException() {
        expectedException.expect(SignPlaceNotFoundException::class.java)

        val path = platformPath(SAMPLES_DIR + "/no-place.jpg")
        signDetector.detectSign(Paths.get(path).toAbsolutePath().toString())
    }
}