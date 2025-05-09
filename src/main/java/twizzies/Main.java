package twizzies;

import org.opencv.core.Core;

public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// showCMD(readImage("opencv.png"));

		// showImageVector(separateColorsChannelsrGrayscale(readImage("bgr.png")));
		// showImageVector(separateColorsChannelsrRGB(readImage("bgr.png")));

		// showImageVector(transposeToHSV(readImage("hsv.png")));

		// showImage("Smoothed Thresheld
		// Image",thresholding(readImage("circles.jpg"),160,180));
		// showImage("Smoothed Multi-Thresheld Image",
		// thresholdingMultipleColors(readImage("circles.jpg"),0,10,160,180));

		// showImage("Extracted Contours",
		// extractContours(thresholdingMultipleColors(readImage("circles.jpg"),0,10,160,180)));

		// showImageVector(surroundCircles(readImage("circles_rectangles.jpg"),0,10,160,180));
		// showImageVector(surroundCircles(readImage("Billard_Balls.jpg"),0,10,160,180));

		// matching(readImage("Billard_Balls.jpg"), readImage(Ball_13.png));
		matchingImageVector(surroundCircles(readImage("Billard_Balls.jpg"), 0, 10, 160, 180),
				readImage("Ball_three.png"));
	}

}
