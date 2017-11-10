package com.minivision.fdi.common;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

  public static BufferedImage getBufferedImage(byte[] img) throws IOException{
    ByteArrayInputStream inputStream = new ByteArrayInputStream(img);
    return ImageIO.read(inputStream);
  }
  
  public static byte[] writeImageToBytes(BufferedImage image, String format) throws IOException{
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
    writer.setOutput(ImageIO.createImageOutputStream(output));
    writer.write(image);
    return output.toByteArray();
  }
  
  public static BufferedImage resize(BufferedImage img, int newW, int newH) {
    int w = img.getWidth();
    int h = img.getHeight();
    BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
    Graphics2D g = dimg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
    g.dispose();
    return dimg;
  }
  
  private static final int MAX_LENGTH = 800;
  
  public static BufferedImage resize(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();
    BufferedImage image_new;
    if (width > MAX_LENGTH && height < MAX_LENGTH) {
        image_new = resize(image, MAX_LENGTH, (int) height * MAX_LENGTH / width);
    } else if (width < MAX_LENGTH && height > MAX_LENGTH) {
        image_new = resize(image, (int) width * MAX_LENGTH / height, MAX_LENGTH);
    } else if (width > MAX_LENGTH && height > MAX_LENGTH) {
        if (width > height) {
            image_new = resize(image, MAX_LENGTH, (int) height * MAX_LENGTH / width);
        } else {
            image_new = resize(image, (int) width * MAX_LENGTH / height, MAX_LENGTH);
        }
    } else {
        image_new = image;
    }
    return image_new;
  }

  public static BufferedImage enlarge(BufferedImage image,int left,int top,int width,int height) {
    int bufferWidth = image.getWidth();
    int bufferHeight = image.getHeight();
    int xSize = (int)(0.5*width);
    int rSize = (int)(0.5*xSize);

    left = left - rSize <= 0? 0:left - rSize;
    top =  top - xSize <= 0 ? 0:top - xSize;

    width = left + width + xSize >= bufferWidth? bufferWidth-left : width  + xSize;
    height = top + height + xSize >= bufferHeight? bufferHeight-top : height + xSize;

    image = image.getSubimage(left,top,width,height);
    return image;
  }
}
