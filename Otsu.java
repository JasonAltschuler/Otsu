/******************************************************************************
 * @author Jason Altschuler
 *
 * @tags image thresholding, image analysis, computer vision, machine learning
 *
 * PURPOSE: Finds optimal threshold between foreground and background pixels
 * in images.
 *
 * ALGORITHM: Otsu's Method (also called "Optimal Global Threshold Calculator")
 *
 * RUN TIME: O(N) where N is the number of pixels in the image.
 *
 * For full documentation, see README
 *****************************************************************************/

package threshold;

import grayscale.Grayscale;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.ImageViewer;
import util.Threshold;


public class Otsu extends GlobalThresholding {
   
   /***********************************************************************
    * Constructors
    **********************************************************************/

   /**
    * Finds optimal global FG/BG threshold for a BufferedImage.
    * <P> All work is done in constructor.
    * @param image
    */
   public Otsu(BufferedImage image) {
      int[][] pixels = Grayscale.imgToGrayPixels(image);
      threshold(pixels);
   }


   /**
    * Finds optimal global FG/BG threshold for array of grayscale pixel intensities.
    * <P> All work is done in constructor.
    * @param pixels
    */
   public Otsu(int[][] pixels) {
      threshold(pixels);
    }

   /***********************************************************************
    * Otsu's method -- the algorithm itself
    **********************************************************************/

   /**
    * Runs Otsu's method.
    * @param pixels
    */
   protected void threshold(int[][] pixels) {
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
      return Threshold.applyThreshold(pixels, threshold);
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
      int[][] pixels = Grayscale.imgToGrayPixels(originalImage);
      
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
      BufferedImage grayscaleImage = Grayscale.toGray(originalImage);
     
      BufferedImage[] toShow = {originalImage, grayscaleImage, thresholdedImage};
      String title = "Otsu's method by Jason Altschuler";
      ImageViewer.showImages(toShow, title, 2, 2);
   }
}
