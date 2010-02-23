package org.ttdc.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class ImageUtils {
	public static BufferedImage toBufferedImageAlt(Image image) {
		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		bufferedImage.createGraphics().drawImage(image, 0, 0, null);

		return bufferedImage;
	}

	public static byte[] bufferedImageToByteArray(BufferedImage img)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(img, "PNG", os);
		return os.toByteArray();
	}

}
