/*
Copyright © 2016-2017 Leejae Karinja

This file is part of Java ASCII Image.

Java ASCII Image is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Java ASCII Image is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Java ASCII Image.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.awt.image.BufferedImage;


public class Main {

	public static void main(String[] args) {
		ImageHandler original = new ImageHandler("Test.png");
		ImageViewer viewOriginal = new ImageViewer(original.getImageBuffer(), true);
		viewOriginal.drawImage();
		BufferedImage sampled = original.scale(25, 300);
		ImageViewer viewSampled = new ImageViewer(sampled, true);
		viewSampled.drawImage();
		ImageHandler sampledImage = new ImageHandler(sampled);
		sampledImage.saveToFile("TestResult.png");
		return;
	}

}
