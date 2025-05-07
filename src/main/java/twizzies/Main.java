package twizzies;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// showCMD(readImage("opencv.png"));
		
		// showColorChannels(separateColorsChannelsrGrayscale(readImage("bgr.png")));
		// showColorChannels(separateColorsChannelsrRGB(readImage("bgr.png")));
		
		// showColorChannels(transposeToHSV(readImage("hsv.png")));
		
		// showImage("Smoothed Thresheld Image",thresholding(readImage("circles.jpg"),0,10));
		
		// seuillage rouge (avec lissage) complet
		// seuillageRouge("circles.jpg");
		
		//extraireContourRouge("circles.jpg");
		//extraireCercleRouge("circles_rectangles.jpg");
		//extraireCercleRouge("Billard_Balls.jpg");
	}

	public static Mat readImage(String fichier) {
		File f = new File(fichier);
		Mat m = Highgui.imread(f.getAbsolutePath());
		return m;
	}

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
	
	public static Vector<Mat> separateColorsChannelsrGrayscale(Mat img){
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(img, channels);
		// BGR order
		return channels;
	}
	
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
	
	public static void showColorChannels(Vector<Mat> channels) {
		for (int i = 0; i < channels.size(); i++) {
			showImage(Integer.toString(i), channels.get(i));
		}
		
	}

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
		Red: 0-10 and 160-180
		Orange: 11-25
		Yellow: 26-35
		Green: 36-85
		Blue: 101-130
		Purple: 131-145
		Pink: 146-159
	*/
	public static Mat thresholding(Mat img, int lower_bound, int higher_bound) {
		Mat hsv_image = Mat.zeros(img.size(), img.type());
		Imgproc.cvtColor(img, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, new Scalar(lower_bound, 100, 100), new Scalar(higher_bound, 255, 255), threshold_img);
		// Smoothing
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
		return threshold_img;
	}

	// -------------------------------------------------------------------------
	/*public static void seuillageRouge(String fichier) {
		Mat m = readImage(fichier);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), threshold_img1);
		Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), threshold_img2);
		Core.bitwise_or(threshold_img1, threshold_img2, threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
		showImage("CercleRouge", threshold_img);
	}

	public static Mat DetecterCercles(Mat hsv_image) {

		Mat threshold_img = new Mat();
		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), threshold_img1);
		Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), threshold_img2);
		Core.bitwise_or(threshold_img1, threshold_img2, threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
		// ImShow("CercleRouge",threshold_img);
		return threshold_img;

	}

	public static void extraireContourRouge(String fichier) {
		Mat m = readImage(fichier);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = DetecterCercles(hsv_image);
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
		showImage("Contours", drawing);
	}

	public static List<MatOfPoint> DetecterContours(Mat threshold_img) {
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

	public static void extraireCercleRouge(String fichier) {
		Mat m = readImage(fichier);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = DetecterCercles(hsv_image);

		List<MatOfPoint> contours = DetecterContours(threshold_img);
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f();

		float[] radius = new float[1];
		Point center = new Point();
		for (int c = 0; c < contours.size(); c++) {
			MatOfPoint contour = contours.get(c);
			double contourArea = Imgproc.contourArea(contour);
			matOfPoint2f.fromList(contour.toList());
			Imgproc.minEnclosingCircle(matOfPoint2f, center, radius);
			if ((contourArea / (Math.PI * radius[0] * radius[0])) >= 0.8) {
				Core.circle(m, center, (int) radius[0], new Scalar(0, 255, 0), 2);
			}
			showImage("Dettection cercles rouges", m);
		}
	}

	public static void matching(String fichier) {
		// La mise à l'échelle
		Mat sroadSign = Highgui.imread(objectfile);
		Mat sObject = new Mat();
		Imgproc.resize(object, sObject, sroadSign.size());
		Mat grayObject = new Mat(sObject.rows(), sObject.cols(), sObject.type());
		Imgproc.cvtColor(sObject, grayObject, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(grayObject, grayObject, 0, 255, Core.NORM_MINMAX);
		
		Mat graySign = new Mat(sroadSign.rows(), sroadSign.cols(), sroadSign.type());
		Imgproc.cvtColor(sroadSign, graySign, Imgproc.COLOR_BGRA2GRAY);
		Core.normalize(graySign, graySign, 0, 255, Core.NORM_MINMAX);
		
		// Extraction des descripteurs et keypoints
		FeatureDetector orbDetector = FeatureDetector.create(FeatureDetector.ORB);
		DescriptorExtractor orbExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		
		MatOfKeyPoint objectKeypoints = new MatOfKeyPoint();
		orbDetector.detect(grayObject, objectKeypoints);
		
		MatOfKeyPoint signKeypoints = new MatOfKeyPoint();
		orbDetector.detect(graySign, signKeypoints);
		
		MatOfKeyPoint objectDescriptor = new MatOfKeyPoint();
		orbDetector.detect(grayObject, objectKeypoints, objectDescriptor);
		
		Mat signDescriptor = new Mat(sroadSign.rows(), sroadSign.cols(), sroadSign.type());
		orbExtractor.compute(graySign, signKeypoints, signDescriptor);
		
		// Faire le matching
		MatOfDMatch matchs = new MatOfDMatch();
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		matcher.match(objectDescriptor, signDescriptor, matchs);
		System.out.println(matchs.dump());
		Mat matchedImage = new Mat(sroadSign.rows(), sroadSign.cols() * 2, sroadSign.type());
		Features2d.drawMatches(sObject, objectKeypoints, sroadSign, signKeypoints, matchs, matchedImage);
	}*/
}