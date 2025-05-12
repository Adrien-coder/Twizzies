package twizzies;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.highgui.Highgui;
import java.io.File;

public class TraitementImageTest {

    @BeforeClass
    public static void setup() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // Tests pour readImage()
    @Test
    public void testReadImage_NullPath() {
        Mat result = TraitementImage.readImage(null);
        assertNull("Devrait retourner null pour un chemin null", result);
    }

   

    @Test
    public void testReadImage_ValidImage() throws Exception {
        // Créer une image test temporaire
        Mat testImage = new Mat(100, 100, CvType.CV_8UC3, new Scalar(255, 0, 0));
        Highgui.imwrite("circles.jpg", testImage);
        
        Mat result = TraitementImage.readImage("circles.jpg");
        assertNotNull("Ne devrait pas retourner null pour une image valide", result);
        assertFalse("La Mat ne devrait pas être vide", result.empty());
        
        // Nettoyer
        new File("circles.jpg").delete();
    }

    // Tests pour thresholding()
    @Test
    public void testThresholding_NullInput() {
        Mat result = TraitementImage.thresholding(null, 0, 10);
        assertNull("Devrait retourner null pour une Mat null", result);
    }

    @Test
    public void testThresholding_EmptyMat() {
        Mat emptyMat = new Mat();
        Mat result = TraitementImage.thresholding(emptyMat, 0, 10);
        assertNotNull("Ne devrait pas retourner null", result);
        assertTrue("Devrait retourner une Mat vide", result.empty());
    }

    @Test
    public void testThresholding_ValidInput() throws Exception {
        // Créer une image test rouge
        Mat testImage = new Mat(100, 100, CvType.CV_8UC3, new Scalar(0, 0, 255));
        Highgui.imwrite("circles.jpg", testImage);
        
        Mat loadedImage = TraitementImage.readImage("circles.jpg");
        Mat result = TraitementImage.thresholding(loadedImage, 0, 10); // Seuillage pour rouge
        
        assertNotNull(result);
        assertFalse(result.empty());
        
        // Vérifier que le seuillage a fonctionné
        boolean hasWhitePixels = false;
        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.cols(); j++) {
                if (result.get(i, j)[0] == 255) {
                    hasWhitePixels = true;
                    break;
                }
            }
            if (hasWhitePixels) break;
        }
        assertTrue("Devrait trouver des pixels blancs après seuillage", hasWhitePixels);
        
        // Nettoyer
        new File("circles.jpg").delete();
    }

    // Tests pour matching()
    @Test
    public void testMatching_NullInputs() {
        float result = TraitementImage.matching(null, null);
        assertEquals("Devrait retourner 0 pour deux Mat null", 0.0f, result, 0.001f);
    }

    @Test
    public void testMatching_OneNullInput() {
        Mat testImage = new Mat(50, 50, CvType.CV_8UC3, new Scalar(0, 255, 0));
        float result1 = TraitementImage.matching(testImage, null);
        float result2 = TraitementImage.matching(null, testImage);
        assertEquals("Devrait retourner 0 quand première Mat est null", 0.0f, result1, 0.001f);
        assertEquals("Devrait retourner 0 quand seconde Mat est null", 0.0f, result2, 0.001f);
    }

    @Test
    public void testMatching_EmptyMats() {
        Mat empty1 = new Mat();
        Mat empty2 = new Mat();
        float result = TraitementImage.matching(empty1, empty2);
        assertEquals("Devrait retourner 0 pour deux Mat vides", 0.0f, result, 0.001f);
    }

 
   }