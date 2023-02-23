// TODO: should this be a class?
// TODO: what package should this be in?

// TODO: needs header and documentation

package grayscale;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class Grayscale {

   /**
    * TODO: optimize with http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
    * also see: http://stackoverflow.com/questions/2615522/java-bufferedimage-getting-red-green-and-blue-individually
    * and see: http://zerocool.is-a-geek.net/?p=329    !!!!!!!!
    * @param image
    * @return
    */
   public static int[][] imgToGrayPixels(BufferedImage image) {
      // cache-efficient for both BufferedImage and int[][]
      int[][] pixels = new int[image.getHeight()][image.getWidth()];
      for (int row = 0; row < image.getHeight(); row++)
         for (int col = 0; col < image.getWidth(); col++) 
            pixels[row][col] = image.getRGB(col, row)& 0xFF; // grayscale value
      return pixels;
   }
   

   // TODO: check that this works with all types of images (not just RGB images)
   // TODO: better name
   public static BufferedImage toGray(BufferedImage RGBimage) {
      BufferedImage gray = new BufferedImage(RGBimage.getWidth(), RGBimage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      ColorConvertOp op = new ColorConvertOp(RGBimage.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
      op.filter(RGBimage, gray);
      
      return gray;
   }
   
   
}
