package twizzies;

import java.io.File;
import java.util.Vector;
import org.opencv.core.Core;
import org.opencv.core.Mat;


public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		try {
            String relativePath = "lib/opencv_ffmpeg2413_64.dll";
            String projectPath = new File("").getAbsolutePath();
            String fullPath = projectPath + File.separator + relativePath;
            System.load(fullPath);
        } catch (Throwable e) {
            System.err.println("Erreur de chargement de la DLL: " + e.getMessage());
        }
		
		Frame f = new Frame();
	}

}
