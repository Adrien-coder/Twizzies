package twizzies;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;

public class MatchingTest {

    @Test
    void testSizeZero() {

        Mat MatriceVide = new Mat();
        Mat MatriceVide2 = new Mat();
        float matchValue = TraitementImage.matching(MatriceVide2, MatriceVide);
        assertEquals(0, matchValue, 0.001);
    }

}
