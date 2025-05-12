package twizzies;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
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
import org.opencv.highgui.VideoCapture;
public class TraitementImage {

    // fonction qui permet de transformer un fichier image en une matrice
    public static Mat readImage(String fichier) {
    	 // Gestion explicite du cas null
        if (fichier == null) {
            return null;
        }
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
    	  // Gestion défensive des entrées null
        if (img == null) {
            return null;
        }
    	
    	// Gestion des cas null et vide
        if (img == null || img.empty()) {
            return new Mat(); // Retourne une nouvelle Mat vide
       }
        
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
    	  // Gestion des cas null
        if (sroadSign == null || object == null) {
            return 0.0f;
        }
        
        if (sroadSign.empty() || object.empty()) {
            return 0.0f;
        }
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
    public static double PourcentageNetB(Mat sroadSign, Mat object) {
        sroadSign = surroundCircles(sroadSign, 0, 10, 160, 180).get(0);

        double matchingValue = 0;
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

        Mat NBObject = new Mat();
        // on enleve le contour rouge
        Mat NBObject1 = thresholdingMultipleColors(sObject, 0, 10, 160, 180);

        Mat NBSignRoad = new Mat();
        Mat NBSignRoad1 = thresholdingMultipleColors(sroadSign, 0, 10, 160, 180);

        Imgproc.cvtColor(sroadSign, NBSignRoad, Imgproc.COLOR_BGR2GRAY);
        Core.normalize(NBSignRoad, NBSignRoad, 0, 255, Core.NORM_MINMAX);
        Imgproc.cvtColor(sObject, NBObject, Imgproc.COLOR_BGR2GRAY);
        Core.normalize(NBObject, NBObject, 0, 255, Core.NORM_MINMAX);

        Core.bitwise_or(NBSignRoad, NBSignRoad1, NBSignRoad);
        Core.bitwise_or(NBObject, NBObject1, NBObject);

        // on vient couper les matrice pour seulement avoir l'interieur des cercles
        NBObject = NBObject.submat(40, NBObject.height() - 40, 40, NBObject.width() - 40);
        NBSignRoad = NBSignRoad.submat(40, NBSignRoad.height() - 40, 40, NBSignRoad.width() - 40);

        // on limite la recherche à l'interieur du cercle (on les initialise en double
        // pour la division)
        double c1 = 0;
        double c2 = 0;
        for (int i = 0; i < NBObject.height(); i++) {
            for (int j = 0; j < NBObject.width(); j++) {
                double[] pixel = NBObject.get(i, j);

                if (pixel[0] == 255) {
                    pixelNobject++;
                }
                c1++;
            }
        }
        for (int i = 0; i < NBSignRoad.height(); i++) {
            for (int j = 0; j < NBSignRoad.width(); j++) {
                double[] pixel = NBSignRoad.get(i, j);
                if (pixel[0] == 255) {
                    pixelNRoad++;
                }
                c2++;
            }
        }
        matchingValue = Math.abs((pixelNRoad / c2) - (pixelNobject / c1));
        System.out.println(matchingValue);
        return matchingValue;
    }

    public static int matchingtrafficSignV2(Mat object) {
        File dossierRef2 = new File("ref");
        File[] listeRef2 = dossierRef2.listFiles();
        Vector<Double> matchingValuesV2 = new Vector<Double>();
        for (File reference : listeRef2) {
            double b = PourcentageNetB(readFile(reference), object);
            matchingValuesV2.add(b);
        }

        // detection de la meilleur correspondance par 'vote'
        double minValue = Collections.min(matchingValuesV2);
        int indiceOfBestMatchV2 = 0;
        double value = matchingValuesV2.get(indiceOfBestMatchV2);
        while (value != minValue) {
            indiceOfBestMatchV2++;
            value = matchingValuesV2.get(indiceOfBestMatchV2);
        }
        System.out.println(listeRef2[indiceOfBestMatchV2].getName());
        return indiceOfBestMatchV2;

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
            int b = matchingtrafficSignV2(imgs.get(i));
            results.add(readFile(listeRef[b]).clone());
        }

        return results;
    }
    
    
    
    
    
    /**
     * Échantillonne et traite les images d'une vidéo
     * 
     * @param cheminVideo Chemin du fichier vidéo
     * @param tauxEchantillonnage Nombre d'images à sauter entre chaque traitement 
     *                            (1 = traiter chaque image, 2 = traiter une image sur deux, etc.)
     * @return Liste des panneaux détectés
     */
    public static Vector<Mat> echantillonnerEtTraiterVideo(String cheminVideo, int tauxEchantillonnage) {
        // Vecteur pour stocker les panneaux détectés
        Vector<Mat> panneauxDetectes = new Vector<>();
        
        // Ouverture de la capture vidéo
        VideoCapture camera = new VideoCapture(cheminVideo);
        
        // Vérification de l'ouverture de la vidéo
        if (!camera.isOpened()) {
            System.err.println("Erreur : Impossible d'ouvrir le fichier vidéo");
            return panneauxDetectes;
        }
        
        // Matrice pour stocker chaque image
        Mat image = new Mat();
        int compteurImages = 0;
        
        try {
            // Lecture de chaque image de la vidéo
            while (camera.read(image)) {
                // Échantillonnage des images selon le taux spécifié
                if (compteurImages % tauxEchantillonnage == 0) {
                    // Détecter les panneaux dans l'image
                    Vector<Mat> resultatsDetection = TraitementImage.DetectSign(cheminVideo);
                    
                    // Si des panneaux sont détectés (en excluant l'image originale)
                    if (resultatsDetection.size() > 1) {
                        // Ajouter les panneaux détectés (à partir du 2ème élément)
                        for (int i = 1; i < resultatsDetection.size(); i++) {
                            panneauxDetectes.add(resultatsDetection.get(i));
                        }
                    }
                }
                
                compteurImages++;
            }
        } finally {
            // Libération des ressources de la capture vidéo
            camera.release();
        }
        
        return panneauxDetectes;
    }
    
    public static Mat DetectSignV3(Mat image) {
        // panneaux de reference
        File dossierRef = new File("ref");
        File[] listeRef = dossierRef.listFiles();

        Vector<Mat> results = new Vector<Mat>();

        results.add(image.clone());
        // detect Red circles
        Vector<Mat> imgs = surroundCircles(image, 0, 10, 160, 180);

        for (int i = 0; i < imgs.size(); i++) {
            int a = matchingtrafficSign(imgs.get(i));
            results.add(readFile(listeRef[a]).clone());
        }
        
        Mat resultat = surroundCirclesinImage(image, 0, 10, 160, 180);

        return resultat;
    }
    
    public static Mat surroundCirclesinImage(Mat img, int lower_bound1, int higher_bound1, int lower_bound2, int higher_bound2) {
        Mat threshold_img = thresholdingMultipleColors(img, lower_bound1, higher_bound1, lower_bound2, higher_bound2);

        List<MatOfPoint> contours = extractContoursPoints(threshold_img);
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();

        float[] radius = new float[1];
        Point center = new Point();
        
        for (int c = 0; c < contours.size(); c++) {
            MatOfPoint contour = contours.get(c);
            double contourArea = Imgproc.contourArea(contour);
            matOfPoint2f.fromList(contour.toList());
            Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);

            if ((contourArea / (Math.PI * radius[0] * radius[0])) >= 0.8) {
                Core.circle(img, center, (int) radius[0], new Scalar(0, 255, 0), 2);
            }
        }
        return img;
    }

	public static Vector<Mat> videoTreatment(String videoPath, int sampleRate) {
	    // Vecteur pour stocker les panneaux détectés
	    Vector<Mat> treatedImages = new Vector<>();
	    
	    File videoFile = new File(videoPath);
	    if (!videoFile.exists()) {
	        System.err.println("Error: File does not exist at path: " + videoPath);
	        return treatedImages;
	    }
	    
	    // Ouverture de la capture vidéo
	    VideoCapture camera = new VideoCapture(videoPath);
	    
	    // Vérification de l'ouverture de la vidéo
	    if (!camera.isOpened()) {
	        System.err.println("Erreur : Impossible d'ouvrir le fichier vidéo");
	        return treatedImages;
	    }
	    
	    // Matrice pour stocker chaque image
	    Mat image = new Mat();
	    int imagesCounter = 0;
	    
	    try {
	        // Lecture de chaque image de la vidéo
	        while (camera.read(image)) {
	            // Échantillonnage des images selon le taux spécifié
	            if (imagesCounter % sampleRate == 0) {
	                // Détecter les panneaux dans l'image
	                Mat detectionResult = DetectSignV3(image);
	                
	                // Ajouter les images traitées au vecteur de résultats
	                treatedImages.add(detectionResult);
	            }
	            
	            imagesCounter++;
	        }
	    } finally {
	        // Libération des ressources de la capture vidéo
	        camera.release();
	    }
	    
	    return treatedImages;
	}
	
	public static void afficherImagesAvecTaux(Vector<Mat> images, int taux) {
        if (images.isEmpty()) {
            System.err.println("Erreur : Le vecteur d'images est vide");
            return;
        }

        JFrame frame = new JFrame("Image");
        JLabel label = new JLabel();
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        for (Mat image : images) {
            if (image.empty()) {
                System.err.println("Erreur : Une des images est vide");
                continue;
            }

            showImage("1", image);
            
            try {
            	Thread.sleep(taux); // attente en millisecondes
            } catch (InterruptedException e) {
                    e.printStackTrace();
            }
        }

        frame.dispose(); // Fermer la fenêtre après l'affichage
    }

    private static BufferedImage matToBufferedImage(Mat mat) {
        // Convertit une Mat OpenCV (type CV_8UC3) en BufferedImage
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        mat.get(0, 0, sourcePixels);

        BufferedImage image;
        if (channels == 3) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }

        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        return image;
    }
}
