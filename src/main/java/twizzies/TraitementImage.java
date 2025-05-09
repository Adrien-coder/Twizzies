package twizzies;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class TraitementImage {

    // fonction qui permet de transformer un fichier image en une matrice
    public static Mat readImage(String fichier) {
        File f = new File(fichier);
        Mat m = Highgui.imread(f.getAbsolutePath());
        return m;
    }

    public static Mat readFile(File f) {
        Mat m = Highgui.imread(f.getAbsolutePath());
        return m;
    }

    // permet de visualiser l'image dans l'invite de commande (pas utile à notre
    // application)
    public static void showCMD(Mat img) {
        for (int i = 0; i < img.height(); i++) {
            for (int j = 0; j < img.width(); j++) {
                double[] rgb = img.get(i, j);
                if (rgb[0] == 255 & rgb[1] == 255 & rgb[2] == 255) {
                    System.out.print(".");
                } else {
                    System.out.print("+");
                }
            }
            System.out.print("\n");
        }
    }

    // permet d'afficherr l'image, ceci est géré par l'interface
    public static void showImage(String title, Mat img) {
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".png", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.setTitle(title);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // separe les canaux bgr de l'image, renvoi trois matrices en noir et blanc
    public static Vector<Mat> separateColorsChannelsrGrayscale(Mat img) {
        Vector<Mat> channels = new Vector<Mat>();
        Core.split(img, channels);
        // BGR order
        return channels;
    }

    // separe les canaux bgr de l'image, renvoi trois matrices en couleur
    public static Vector<Mat> separateColorsChannelsrRGB(Mat img) {
        Vector<Mat> channels = separateColorsChannelsrGrayscale(img);
        Vector<Mat> results = new Vector<Mat>();
        // BGR order
        Mat dst = Mat.zeros(img.size(), img.type());
        Vector<Mat> chans = new Vector<Mat>();
        Mat empty = Mat.zeros(img.size(), CvType.CV_8UC1);
        for (int i = 0; i < channels.size(); i++) {
            chans.removeAllElements();
            for (int j = 0; j < channels.size(); j++) {
                if (j != i) {
                    chans.add(empty);
                } else {
                    chans.add(channels.get(i));
                }
            }
            Core.merge(chans, dst);
            results.add(dst.clone());
        }
        return results;
    }

    // Visualisation de l'image , gerer par l'interface
    public static void showImageVector(Vector<Mat> imgs) {
        for (int i = 0; i < imgs.size(); i++) {
            showImage(Integer.toString(i), imgs.get(i));
        }
    }

    // transformation en HSV
    public static Vector<Mat> transposeToHSV(Mat img) {
        Mat output = Mat.zeros(img.size(), img.type());
        Imgproc.cvtColor(img, output, Imgproc.COLOR_BGR2HSV);
        Vector<Mat> channels = new Vector<Mat>();
        Core.split(output, channels);
        double hsv_values[][] = { { 1, 255, 255 }, { 179, 1, 255 }, { 179, 0, 1 } };
        Vector<Mat> results = new Vector<Mat>();
        for (int i = 0; i < 3; i++) {
            Mat chans[] = new Mat[3];
            for (int j = 0; j < 3; j++) {
                Mat empty = Mat.ones(img.size(), CvType.CV_8UC1);
                Mat comp = Mat.ones(img.size(), CvType.CV_8UC1);
                Scalar v = new Scalar(hsv_values[i][j]);
                Core.multiply(empty, v, comp);
                chans[j] = comp;
            }
            chans[i] = channels.get(i);
            Mat dst = Mat.zeros(output.size(), output.type());
            Mat res = Mat.ones(dst.size(), dst.type());
            Core.merge(Arrays.asList(chans), dst);
            Imgproc.cvtColor(dst, res, Imgproc.COLOR_HSV2BGR);
            results.add(res.clone());
        }
        return results;
    }

    /*
     * Red: 0-10 and 160-180
     * Orange: 11-25
     * Yellow: 26-35
     * Green: 36-85
     * Blue: 101-130
     * Purple: 131-145
     * Pink: 146-159
     */

    // seuillage de l'image pour un intervalle de couleur
    public static Mat thresholding(Mat img, int lower_bound, int higher_bound) {
        Mat hsv_image = Mat.zeros(img.size(), img.type());
        Imgproc.cvtColor(img, hsv_image, Imgproc.COLOR_BGR2HSV);
        Mat threshold_img = new Mat();
        Core.inRange(hsv_image, new Scalar(lower_bound, 100, 100), new Scalar(higher_bound, 255, 255), threshold_img);
        // Smoothing
        Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
        return threshold_img;
    }

    // seuillage de l'image pour plusieurs couleurs
    public static Mat thresholdingMultipleColors(Mat img, int lower_bound1, int higher_bound1, int lower_bound2,
            int higher_bound2) {
        Mat hsv_image = Mat.zeros(img.size(), img.type());
        Imgproc.cvtColor(img, hsv_image, Imgproc.COLOR_BGR2HSV);
        Mat threshold_img = new Mat();
        Mat threshold_img1 = new Mat();
        Mat threshold_img2 = new Mat();
        Core.inRange(hsv_image, new Scalar(lower_bound1, 100, 100), new Scalar(higher_bound1, 255, 255),
                threshold_img1);
        Core.inRange(hsv_image, new Scalar(lower_bound2, 100, 100), new Scalar(higher_bound2, 255, 255),
                threshold_img2);
        Core.bitwise_or(threshold_img1, threshold_img2, threshold_img);
        // Smoothing
        Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
        return threshold_img;
    }

    // extraction des contours
    public static Mat extractContours(Mat threshold_img) {
        int thresh = 100;
        Mat canny_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfInt4 hierarchy = new MatOfInt4();
        Imgproc.Canny(threshold_img, canny_output, thresh, thresh * 2);
        Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat drawing = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
        Random rand = new Random();
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1));
            Imgproc.drawContours(drawing, contours, i, color, 1, 8, hierarchy, 0, new Point());
        }
        return drawing;
    }

    // extraction des contours
    public static List<MatOfPoint> extractContoursPoints(Mat threshold_img) {
        int thresh = 100;
        Mat canny_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        MatOfInt4 hierarchy = new MatOfInt4();
        Imgproc.Canny(threshold_img, canny_output, thresh, thresh * 2);
        Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        Mat drawing = Mat.zeros(canny_output.size(), CvType.CV_8UC3);
        Random rand = new Random();
        for (int i = 0; i < contours.size(); i++) {
            Scalar color = new Scalar(rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1), rand.nextInt(255 - 0 + 1));
            Imgproc.drawContours(drawing, contours, i, color, 1, 8, hierarchy, 0, new Point());
        }
        return contours;
    }

    // detection des cercles
    public static Vector<Mat> surroundCircles(Mat img, int lower_bound1, int higher_bound1, int lower_bound2,
            int higher_bound2) {
        Mat threshold_img = thresholdingMultipleColors(img, lower_bound1, higher_bound1, lower_bound2, higher_bound2);

        List<MatOfPoint> contours = extractContoursPoints(threshold_img);
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();

        float[] radius = new float[1];
        Point center = new Point();
        Vector<Mat> results = new Vector<Mat>();
        for (int c = 0; c < contours.size(); c++) {
            MatOfPoint contour = contours.get(c);
            double contourArea = Imgproc.contourArea(contour);
            matOfPoint2f.fromList(contour.toList());
            Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
            if ((contourArea / (Math.PI * radius[0] * radius[0])) >= 0.8) {
                Core.circle(img, center, (int) radius[0], new Scalar(0, 255, 0), 2);
                Rect rect = Imgproc.boundingRect(contour);
                Core.rectangle(img, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0), 2);
                Mat tmp = img.submat(rect.y, rect.y + rect.height, rect.x, rect.x + rect.width);
                if (tmp.size().width < 75 || tmp.size().height < 75) {
                    Imgproc.resize(tmp, tmp, new Size(tmp.size().width * 3, tmp.size().height * 3));
                }
                results.add(tmp.clone());
            }
        }
        return results;
    }

    // matching entre deux image
    public static float matching(Mat sroadSign, Mat object) {

        if (sroadSign.dims() < 1 || object.dims() < 1) {
            System.out.println("here");
            return 0;

        }

        // Mise à l'échelle
        Mat sObject = new Mat();
        Imgproc.resize(object, sObject, sroadSign.size());

        // Conversion en niveaux de gris et normalisation
        Mat grayObject = new Mat();
        Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGR2GRAY);
        Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);

        Mat graySign = new Mat();
        Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGR2GRAY);
        Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);

        // Détection de points clés (keypoints) et extraction descripteurs ORB
        FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
        MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
        orbDetector.detect(grayObject, objectKeypoints);
        orbDetector.detect(graySign, signKeypoints);

        Mat objectDescriptor = new Mat();
        Mat signDescriptor = new Mat();
        orbExtractor.compute(grayObject, objectKeypoints, objectDescriptor);
        orbExtractor.compute(graySign, signKeypoints, signDescriptor);

        // Matching avec BruteForce
        MatOfDMatch matches = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(objectDescriptor, signDescriptor, matches);

        float sum = 0;
        for (DMatch match : matches.toList()) {
            sum += match.distance;
        }
        // plus le nombre est petit plus il y a de correspondance
        float MatchingValue = sum / matches.rows();

        Mat matchedImage = new Mat(sroadSign.rows(), sroadSign.cols() * 2, sroadSign.type());
        Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matches, matchedImage);
        // showImage("Matching", matchedImage);

        return MatchingValue;
    }

    public static void matchingImageVector(Vector<Mat> imgs, Mat object) {
        for (int i = 0; i < imgs.size(); i++) {
            matching(imgs.get(i), object);
        }
    }

    // retourne l'indice du panneau de reference Correspondant le mieux
    public static int matchingtrafficSign(Mat object) {

        File dossierRef = new File("ref");
        File[] listeRef = dossierRef.listFiles();
        Vector<Float> matchingValues = new Vector<Float>();
        for (File reference : listeRef) {
            float a = matching(readFile(reference), object);
            matchingValues.add(a);
        }

        // detection de la meilleur correspondance par 'vote'
        float minValue = Collections.min(matchingValues);
        int indiceOfBestMatch = 0;
        float value = matchingValues.get(indiceOfBestMatch);
        while (value != minValue) {
            indiceOfBestMatch++;
            value = matchingValues.get(indiceOfBestMatch);
        }
        System.out.println(listeRef[indiceOfBestMatch].getName());
        return indiceOfBestMatch;

    }

    public static Vector<Mat> DetectSign(String img) {
        // panneaux de reference
        File dossierRef = new File("ref");
        File[] listeRef = dossierRef.listFiles();

        Vector<Mat> results = new Vector<Mat>();
        // convert to Mat
        Mat image = readImage(img);

        results.add(image.clone());
        // detect Red circles
        Vector<Mat> imgs = surroundCircles(image, 0, 10, 160, 180);

        for (int i = 0; i < imgs.size(); i++) {
            int a = matchingtrafficSign(imgs.get(i));
            results.add(readFile(listeRef[a]).clone());
        }

        return results;
    }

    // Renvoi la difference en valeur absolue de pixels noirs entre deux image
    public static float PourcentageNetB(Mat sroadSign, Mat object) {
        float matchingValue = 0;
        if (sroadSign.dims() < 1 || object.dims() < 1) {
            System.out.println("here");
            return 0;

        }

        // Mise à l'échelle
        Mat sObject = new Mat();
        Imgproc.resize(object, sObject, sroadSign.size());

        // Conversion en niveaux de gris et normalisation
        int pixelNobject = 0;
        int pixelNRoad = 0;
        Mat NBObject = thresholdingMultipleColors(object, 0, 10, 160, 180);
        Mat NBSignRoad = thresholdingMultipleColors(sroadSign, 0, 10, 160, 180);
        for (int i = 0; i < NBObject.height(); i++) {
            for (int j = 0; j < NBObject.width(); j++) {
                double[] pixel = NBObject.get(i, j);
                if (pixel[0] == 0) {
                    pixelNobject++;
                }
            }
        }
        for (int i = 0; i < NBSignRoad.height(); i++) {
            for (int j = 0; j < NBSignRoad.width(); j++) {
                double[] pixel = NBSignRoad.get(i, j);
                if (pixel[0] == 0) {
                    pixelNRoad++;
                }
            }
        }
        matchingValue = Math.abs(pixelNobject - pixelNRoad);

        return matchingValue;
    }

    public static int matchingtrafficSignV2(Mat object) {

        File dossierRef = new File("ref");
        File[] listeRef = dossierRef.listFiles();
        Vector<Float> matchingValues = new Vector<Float>();
        for (File reference : listeRef) {
            float a = PourcentageNetB(readFile(reference), object);
            matchingValues.add(a);
        }

        // detection de la meilleur correspondance par 'vote'
        float minValue = Collections.min(matchingValues);
        int indiceOfBestMatch = 0;
        float value = matchingValues.get(indiceOfBestMatch);
        while (value != minValue) {
            indiceOfBestMatch++;
            value = matchingValues.get(indiceOfBestMatch);
        }
        System.out.println(listeRef[indiceOfBestMatch].getName());
        return indiceOfBestMatch;

    }

    public static Vector<Mat> DetectSignV2(String img) {
        // panneaux de reference
        File dossierRef = new File("ref");
        File[] listeRef = dossierRef.listFiles();

        Vector<Mat> results = new Vector<Mat>();
        // convert to Mat
        Mat image = readImage(img);

        results.add(image.clone());
        // detect Red circles
        Vector<Mat> imgs = surroundCircles(image, 0, 10, 160, 180);

        for (int i = 0; i < imgs.size(); i++) {
            int a = matchingtrafficSignV2(imgs.get(i));
            results.add(readFile(listeRef[a]).clone());
        }

        return results;
    }
}
