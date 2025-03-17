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
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("mat = "+mat.dump());
		// affichercmd("opencv.png");
		// sans couleur
		rgbNB("bgr.png");
		// avec couleur
		// separationRgb("bgr.png");
		// hsv
		// passageHSV("hsv.png");
		// seuillage rouge (avec lissage) un seul cercle
		// seuillage("circles.jpg");
		// seuillage rouge (avec lissage) complet
		// seuillageRouge("circles.jpg");
		//extraireContourRouge("circles.jpg");
		//extraireCercleRouge("circles_rectangles.jpg");
		//extraireCercleRouge("Billard_Balls.jpg");
	}

	public static Mat LectureImage(String fichier) {
		File f = new File(fichier);
		Mat m = Highgui.imread(f.getAbsolutePath());
		return m;
	}

	public static void affichercmd(String fichier) {
		Mat m = LectureImage(fichier);
		for (int i = 0; i < m.height(); i++) {
			for (int j = 0; j < m.width(); j++) {
				double[] rgb = m.get(i, j);
				if (rgb[0] == 255 & rgb[1] == 255 & rgb[2] == 255) {
					System.out.print("-");
				} else {
					System.out.print("+");
				}
			}
			System.out.print("\n");
		}
	}

	public static void ImShow(String title, Mat img) {
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

	public static void rgbNB(String fichier) {
		Mat m = LectureImage("bgr.png");
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(m, channels);
		for (int i = 0; i < channels.size(); i++) {
			ImShow(Integer.toBinaryString(i), channels.get(i));
		}
	}

	public static void separationRgb(String fichier) {
		Mat m = LectureImage("bgr.png");
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(m, channels);
		Mat dst = Mat.zeros(m.size(), m.type());
		Vector<Mat> chans = new Vector<Mat>();
		Mat empty = Mat.zeros(m.size(), CvType.CV_8UC1);
		for (int i = 0; i < channels.size(); i++) {
			ImShow(Integer.toString(i), channels.get(i));
			chans.removeAllElements();
			for (int j = 0; j < channels.size(); j++) {
				if (j != i) {
					chans.add(empty);
				} else {
					chans.add(channels.get(i));
				}
			}
			Core.merge(chans, dst);
			ImShow(Integer.toBinaryString(i), dst);
		}

	}

	public static void passageHSV(String fichier) {
		Mat m = LectureImage(fichier);
		Mat output = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, output, Imgproc.COLOR_BGR2HSV);
		ImShow("HSV", output);
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(output, channels);
		double hsv_values[][] = { { 1, 255, 255 }, { 179, 1, 255 }, { 179, 0, 1 } };
		for (int i = 0; i < 3; i++) {
			ImShow(Integer.toString(i) + "-HSV", channels.get(i));
			Mat chans[] = new Mat[3];
			for (int j = 0; j < 3; j++) {
				Mat empty = Mat.ones(m.size(), CvType.CV_8UC1);
				Mat comp = Mat.ones(m.size(), CvType.CV_8UC1);
				Scalar v = new Scalar(hsv_values[i][j]);
				Core.multiply(empty, v, comp);
				chans[j] = comp;
			}
			chans[i] = channels.get(i);
			Mat dst = Mat.zeros(output.size(), output.type());
			Mat res = Mat.ones(dst.size(), dst.type());
			Core.merge(Arrays.asList(chans), dst);
			Imgproc.cvtColor(dst, res, Imgproc.COLOR_HSV2BGR);
			ImShow(Integer.toBinaryString(i), res);
		}
	}

	public static void seuillage(String fichier) {
		Mat m = LectureImage(fichier);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
		ImShow("CercleRouge", threshold_img);

	}

	public static void seuillageRouge(String fichier) {
		Mat m = LectureImage(fichier);
		Mat hsv_image = Mat.zeros(m.size(), m.type());
		Imgproc.cvtColor(m, hsv_image, Imgproc.COLOR_BGR2HSV);
		Mat threshold_img = new Mat();
		Mat threshold_img1 = new Mat();
		Mat threshold_img2 = new Mat();
		Core.inRange(hsv_image, new Scalar(0, 100, 100), new Scalar(10, 255, 255), threshold_img1);
		Core.inRange(hsv_image, new Scalar(160, 100, 100), new Scalar(179, 255, 255), threshold_img2);
		Core.bitwise_or(threshold_img1, threshold_img2, threshold_img);
		Imgproc.GaussianBlur(threshold_img, threshold_img, new Size(9, 9), 2, 2);
		ImShow("CercleRouge", threshold_img);
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
		Mat m = LectureImage(fichier);
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
		ImShow("Contours", drawing);
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
		Mat m = LectureImage(fichier);
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
			ImShow("Dettection cercles rouges", m);
		}
	}

}