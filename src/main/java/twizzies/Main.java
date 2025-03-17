package twizzies;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class Main {
    public static void main(String[] args) {
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		rgbNB("bgr.png");
    }
	
	public static Mat LectureImage(String fichier) {
		File f = new File(fichier);
		Mat m = Highgui.imread(f.getAbsolutePath());
		return m;
	}
	
	public static void afficherCmd(String fichier) {
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
			ByteArrayInputStream in = new ByteArrayInputStream(byteArray);
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
		Mat m = LectureImage(fichier);
		Vector<Mat> channels = new Vector<Mat>();
		Core.split(m, channels);
		for (int i = 0; i < channels.size(); i++) {
			ImShow(Integer.toBinaryString(i), channels.get(i));
		}
	}
    
}