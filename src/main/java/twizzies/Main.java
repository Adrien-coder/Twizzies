package twizzies;

import java.io.File;
import org.opencv.highgui.VideoCapture;


import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.Mat;


public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//Frame f = new Frame();
		
		System.load("C:\\Users\\silva\\Downloads\\opencv\\build\\x64\\vc12\\bin\\opencv_ffmpeg2413_64.dll");
		String testVideo = "video2.mp4";
		
		int sampleRate = (int)(1000.0 / 30);
		TraitementImage.afficherImagesAvecTaux(TraitementImage.videoTreatment("video2.mp4", sampleRate), sampleRate);
	}

}
