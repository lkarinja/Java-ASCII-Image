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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHandler {

	private BufferedImage image;
	private File file;
	private int height;
	private int width;
	private byte[][][] rgbData;

	public ImageHandler() {

	}

	public ImageHandler(String fileName) {
		this.file = new File(fileName);
		this.loadFile();
	}

	public ImageHandler(int height, int width) {
		this.height = height;
		this.width = width;
		this.image = new BufferedImage(this.height, this.width, BufferedImage.TYPE_INT_RGB);
	}

	public ImageHandler(BufferedImage b) {
		this.height = b.getHeight();
		this.width = b.getWidth();
		this.image = b;
	}

	/**
	 * Sets the file of the image and calls loadFile()
	 * 
	 * @param fileName
	 */
	public void setFile(String fileName) {
		this.file = new File(fileName);
		this.loadFile();
		return;
	}

	/**
	 * Loads the Image Data into a byte array of Y, X, Color
	 */
	public void loadFile() {
		try {
			this.image = ImageIO.read(this.file);
			this.height = this.image.getHeight();
			this.width = this.image.getWidth();
			this.calcData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Calculates the rgbData array given the data stored in the BufferedImage
	 */
	public void calcData() {
		this.rgbData = new byte[this.height][this.width][3];
		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				int pixelData = this.image.getRGB(x, y);
				this.rgbData[y][x][0] = (byte) (pixelData >> 16);
				this.rgbData[y][x][1] = (byte) (pixelData >> 8);
				this.rgbData[y][x][2] = (byte) (pixelData);
			}
		}
	}

	/**
	 * Recalculates the image's RGB data from the rgbData array
	 */
	public void recalcBuffer() {
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				int a = 0;
				int r = (this.rgbData[y][x][0] << 16) & 0x00FF0000;
				int g = (this.rgbData[y][x][1] << 8) & 0x0000FF00;
				int b = this.rgbData[y][x][2] & 0x000000FF;
				this.image.setRGB(x, y, 0x00000000 | a | r | g | b);
			}
		}
		return;
	}

	/**
	 * Scales an image from its original size to the specified size
	 * 
	 * @param height New height of the image
	 * @param width New width of the image
	 * @return The downsampled image
	 */
	public BufferedImage scale(int height, int width) {
		//Do not upsample
		if (this.width < width) width = this.width;
		if (this.height < height) height = this.height;

		BufferedImage sampledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		//It cannot be here
		double xStep = ((double) this.width) / width;
		double yStep = ((double) this.height) / height;
		System.out.println("xStep: " + xStep + "\nyStep: " + yStep);

		//For every pixel of the new image...
		for (int sampleX = 0; sampleX < width; sampleX++) {
			for (int sampleY = 0; sampleY < height; sampleY++) {
				long sumR = 0;
				long sumG = 0;
				long sumB = 0;
				

				if(sampleX == 2 && sampleY == 0){
					System.out.print("");
				}

				//Loop from the corresponding xStep by yStep group of pixels in the old image and average their RGB values
				for (int x = (int)(sampleX * xStep); x < (int)((sampleX + 1) * xStep) && x < this.width; x++) {
					//System.out.println("X: " + sampleX + " -> " + x);
					if(sampleX == 2 && sampleY == 0){
						System.out.print("");
					}
					for (int y = (int)(sampleY * yStep); y < (int)((sampleY + 1) * yStep) && y < this.height; y++) {
						//System.out.println("Y: " + sampleY + " -> " + y);
						//oVERFLOW or something ehere
						int pixelData = this.image.getRGB(x, y);
						sumR += (pixelData & 0x00FF0000) >>> 16;
						sumG += (pixelData & 0x0000FF00) >>> 8;
						sumB += (pixelData & 0x000000FF);
					}
				}

				//And set their RGB values to the corresponding x y pixel in the new image
				
				//Think here and stuff
				//double averagedWidth = Math.min(xStep, this.width - (sampleX * xStep));
				//double averagedHeight = Math.min(yStep, this.height - (sampleY * yStep));
				double averagedWidth = Math.min(xStep, (int)((sampleX + 1) * xStep) - (int)((sampleX) * xStep));
				double averagedHeight = Math.min(yStep, (int)((sampleY + 1) * yStep) - (int)((sampleY) * yStep));
				
				double r1 = sumR / Math.round(averagedWidth * averagedHeight);
				double g1 = sumG / Math.round(averagedWidth * averagedHeight);
				double b1 = sumB / Math.round(averagedWidth * averagedHeight);
				//We fixed some stuff here...
				int r = (int)(((sumR / Math.round(averagedWidth * averagedHeight)) << 16) & 0x00FF0000);
				int g = (int)(((sumG / Math.round(averagedWidth * averagedHeight)) << 8) & 0x0000FF00);
				int b = (int)((sumB / Math.round(averagedWidth * averagedHeight)) & 0x000000FF);

				//System.out.println(sampleX + ":" + sampleY);
				//System.out.println("RGB Sum Data: " + sumR + "|" + sumG + "|" + sumB);
				//System.out.println("RGB Data: " + r + "|" + g + "|" + b + " [" + (0x00000000 | r | g | b) + "]");
				if(sampleX == 101 && sampleY == 76){
					System.out.println(sampleX + ":" + sampleY);
					System.out.println("Stuff: " + r1 + "|" + g1 + "|" + b1);
					System.out.println("RGB Sum Data: " + sumR + "|" + sumG + "|" + sumB);
					System.out.println("RGB Data: " + r + "|" + g + "|" + b + " [" + (0x00000000 | r | g | b) + "]");
				}
				if(sampleX == 100 && sampleY == 75){
					System.out.println(sampleX + ":" + sampleY);
					System.out.println("Stuff: " + r1 + "|" + g1 + "|" + b1);
					System.out.println("RGB Sum Data: " + sumR + "|" + sumG + "|" + sumB);
					System.out.println("RGB Data: " + r + "|" + g + "|" + b + " [" + (0x00000000 | r | g | b) + "]");
				}
				sampledImage.setRGB(sampleX, sampleY, 0x00000000 | r | g | b);
			}
		}
		System.out.println("xStep: " + xStep + "\nyStep: " + yStep);
		return sampledImage;

	}

	public BufferedImage getImageBuffer() {
		return this.image;
	}

	public void saveToFile(String fileName) {
		try {
			ImageIO.write(this.image, fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase(), new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
}
