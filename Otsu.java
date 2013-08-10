/******************************************************************************
 * @author Jason Altschuler
 *
 * @tags image thresholding, image analysis, computer vision, machine learning
 *
 * PURPOSE: Finds optimal threshold between foreground and background pixels
 * in images.
 *
 * ALGORITHM: Otsu's Method.
 *
 * RUN TIME: O(N) where N is the number of pixels in the image.
 *
 * For full documentation, see README
 *****************************************************************************/

// TODO: speed check against MATLAB

package otsu;

import grayscale.Grayscale;
import imageio.ImageViewer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


public class Otsu {
   // pixel intensities range from 0 to 255, inclusive, in BufferedImages
   private final static int RADIX = 256;

   /***********************************************************************
    * Fields
    **********************************************************************/
   // Foreground pixels >= threshold; background pixels < threshold
   private int threshold;

   /***********************************************************************
    * Constructors
    **********************************************************************/

   /**
    * Finds optimal FG/BG threshold for a BufferedImage.
    * <P> All work is done in constructor.
    * @param image
    */
   public Otsu(BufferedImage image) {
      int[][] pixels = Grayscale.getGrayPixels(image);
      run(pixels);
   }


   /**
    * Finds optimal FG/BG threshold for array of grayscale pixel intensities.
    * <P> All work is done in constructor.
    * @param pixels
    */
   public Otsu(int[][] pixels) {
      run(pixels);
    }

   /***********************************************************************
    * Otsu's method -- the algorithm itself
    **********************************************************************/

   /**
    * Runs Otsu's method.
    * @param pixels
    */
   private void run(int[][] pixels) {
      // create a histogram out of the pixels
      int[] n_t = histogram(pixels);

      // get sum of all pixel intensities
      int sum = sumIntensities(n_t);

      // perform Otsu's method
      calcThreshold(n_t, pixels.length * pixels[0].length, sum);
   }

   /**
    * Creates a histogram out of the pixels.
    * <P> Run-time: O(N) where N is the number of pixels.
    * @param pixels
    * @return
    */
   private int[] histogram(int[][] pixels) {
      int[] n_t = new int[RADIX];

      for (int i = 0; i < pixels.length; i++)
         for (int j = 0; j < pixels[0].length; j++)
            n_t[pixels[i][j]]++;

      return n_t;
   }


   /**
    * Returns sum of all the pixel intensities in image.
    * <P> Run time: constant (O(RADIX))
    * @param n_t
    * @return
    */
   private int sumIntensities(int[] n_t) {
      int sum = 0;
      for (int i = 0; i < n_t.length; i++)
         sum += i * n_t[i];
      return sum;
   }


   /**
    * The core of Otsu's method.
    * <P> Run-time: constant (O(RADIX))
    */
   private void calcThreshold(int[] n_t, int N, int sum) {
      double variance;                       // objective function to maximize
      double bestVariance = Double.NEGATIVE_INFINITY;

      double mean_bg = 0;
      double weight_bg = 0;

      double mean_fg = (double) sum / (double) N;     // mean of population
      double weight_fg = N;                           // weight of population

      double diff_means;

      // loop through all candidate thresholds
      int t = 0;
      while (t < RADIX) {
         // calculate variance
         diff_means = mean_fg - mean_bg;
         variance = weight_bg * weight_fg * diff_means * diff_means;

         // store best threshold
         if (variance > bestVariance) {
            bestVariance = variance;
            threshold = t;
         }

         // go to next candidate threshold
         while (t < RADIX && n_t[t] == 0)
            t++;

         mean_bg = (mean_bg * weight_bg + n_t[t] * t) / (weight_bg + n_t[t]);
         mean_fg = (mean_fg * weight_fg - n_t[t] * t) / (weight_fg - n_t[t]);
         weight_bg += n_t[t];
         weight_fg -= n_t[t];
         t++;
      }
   }


   /***********************************************************************
    * Accessors
    **********************************************************************/
   public int getThreshold() {
      return threshold;
   }

   /***********************************************************************
    * Unit testing
    ***********************************************************************/

   /**
    * Returns BufferedImage where color at (i, j) is black if pixel intensity >
    * threshold; white otherwise.
    * @param pixels
    * @param threshold
    * @return
    */
   private static BufferedImage applyThreshold(int[][] pixels, int threshold) {
      int width = pixels.length;
      int height = pixels[0].length;

      BufferedImage thresholdedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = thresholdedImage.getRaster();

      int[] black = {0, 0, 0};
      int[] white = {255, 255, 255};

      for (int row = 0; row < height; row++)
         for (int col = 0; col < width; col++)
            raster.setPixel(col, row, pixels[col][row] > threshold ? white : black);

      return thresholdedImage;
   }

   /**
    * Displays images side by side.
    * @param images
    */
   private static void showImages(BufferedImage[] images) {
      ImageViewer gui = new ImageViewer(images);
      gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      gui.setVisible(true);
      gui.pack();
      gui.setTitle("Otsu's Method by Jason Altschuler");
   }

   /**
    * Unit testing
    * @param args
    * @throws IOException
    */
   public static void main(String args[]) throws IOException {
      // read image and get pixels
      String img = args[0];
      BufferedImage originalImage = ImageIO.read(new File(img));
      int[][] pixels = Grayscale.getGrayPixels(originalImage);

      // run Otsu's
      final long startTime = System.currentTimeMillis();
      Otsu otsu = new Otsu(pixels);
      final long endTime = System.currentTimeMillis();
      final double elapsed = (double) (endTime - startTime) / 1000;

      // print result and timing information
      int threshold = otsu.getThreshold();
      System.out.println("Threshold = " + threshold);
      System.out.println("Otsu's method took " + elapsed + " seconds.");

      // display thresholded image
      BufferedImage thresholdedImage = applyThreshold(pixels, threshold);
      BufferedImage grayscaleImage = Grayscale.convertUsingJava(originalImage);
      showImages(new BufferedImage[] {originalImage, grayscaleImage, thresholdedImage});
   }
}
