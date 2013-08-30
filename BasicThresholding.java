/******************************************************************************
 * @author Jason Altschuler
 *
 * @tags image thresholding, image analysis, computer vision, machine learning
 *
 * PURPOSE: Finds optimal threshold between foreground and background pixels
 * in images.
 *
 * ALGORITHM: Basic thresholding heuristic. Less optimal than Otsu's.
 * 
 *    1. Choose initial estimate for global threshold T.
 *    2. Segment image using T into 2 classes: pixels with intensity 
 *    values > T and pixels with intensity values <= T.
 *    3. Compute average (mean) intensity values m_1 and m_2 for pixels
 *    in each of the two classes.
 *    4. Compute a new threshold value: T = 1/2 (m_1 + m_2)
 *    5. Repeat steps 2 through 4 until change in T is less than epsilon.
 *
 * RUN TIME: O(N) where N is # of pixels per image
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
import util.Statistics;
import util.Threshold;


public class BasicThresholding extends GlobalThresholding {
   
   /***********************************************************************
    * Static fields
    **********************************************************************/
   
   // default value for epsilon
   private final static double DEFAULT_EPSILON = 2;
   
   /***********************************************************************
    * Non-static fields
    **********************************************************************/
   
   // stop iterating when change in threshold is less than this
   private double epsilon;
   
   /***********************************************************************
    * Constructors
    **********************************************************************/

   /**
    * Finds global FG/BG threshold given BufferedImage.
    * <P> All work is done in constructor.
    * @param image
    */
   public BasicThresholding(BufferedImage image) {
      int[][] pixels = Grayscale.imgToGrayPixels(image);
      threshold(pixels);
   }
   
   /**
    * Finds global FG/BG threshold given BufferedImage.
    * <P> All work is done in constructor.
    * @param image
    * @param epsilon minimum change in threshold to stop running
    */
   public BasicThresholding(BufferedImage image, double epsilon) {
      int[][] pixels = Grayscale.imgToGrayPixels(image);
      this.epsilon = epsilon;
      threshold(pixels);
   }

   /**
    * Finds global FG/BG threshold given array of grayscale pixel intensities.
    * <P> All work is done in constructor.
    * @param pixels minimum change in threshold to stop running
    */
   public BasicThresholding(int[][] pixels) {
      epsilon = DEFAULT_EPSILON;
      threshold(pixels);
    }

   /**
    * Finds global FG/BG threshold given array of grayscale pixel intensities.
    * <P> All work is done in constructor.
    * @param pixels
    * @param epsilon change in threshold
    */
   public BasicThresholding(int[][] pixels, double epsilon) {
      this.epsilon = epsilon;
      threshold(pixels);
    }
   

   /***********************************************************************
    * Otsu's method -- the algorithm itself
    **********************************************************************/

   /**
    * Calculate appropriate global threshold. Steps:
    *    1. Choose initial estimate for global threshold T.
    *    2. Segment image using T into 2 classes: pixels with intensity 
    *    values > T and pixels with intensity values <= T.
    *    3. Compute average (mean) intensity values m_1 and m_2 for pixels
    *    in each of the two classes.
    *    4. Compute a new threshold value: T = 1/2 (m_1 + m_2)
    *    5. Repeat steps 2 through 4 until change in T is less than epsilon. 
    * @param pixels
    */
   protected void threshold(int[][] pixels) {
      // initial estimate for threshold: median
      int threshold_new = initialEstimate(pixels);
      int threshold_old; 
      
      // mean pixel intensity of each class
      double m_1;
      double m_2;
      
      // # of pixels in each class
      int sz_1;
      int sz_2;
      
      do {
         // reset parameters
         threshold_old = threshold_new;
         m_1 = 0;
         m_2 = 0;
         sz_1 = 0;
         sz_2 = 0;
         
         // classify pixels 
         for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
               if (pixels[i][j] > threshold_old) {
                  m_1 += pixels[i][j];
                  sz_1++;
               } else {
                  m_2 += pixels[i][j];
                  sz_2++;
               }
            }
         }
         
         // calculate means
         m_1 /= sz_1;
         m_2 /= sz_2;
         
         // update threshold
         threshold_new = (int) ((m_1 + m_2) / 2);
         
      } while (Math.abs(threshold_new - threshold_old) < epsilon);
   
      this.threshold = threshold_new;
   }

   
   /**
    * Estimates initial global threshold by calculating mean pixel intensity.
    * @param pixels
    * @return
    */
   private int initialEstimate(int[][] pixels) {
      return (int) Statistics.calcMean(pixels);
   }

   
   /***********************************************************************
    * Accessors
    ***********************************************************************/
   public double getEpsilon() {
      return epsilon;
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
      System.out.println("Thresholding took " + elapsed + " seconds.");

      // display thresholded image
      BufferedImage thresholdedImage = applyThreshold(pixels, threshold);
      BufferedImage grayscaleImage = Grayscale.toGray(originalImage);
     
      BufferedImage[] toShow = {originalImage, grayscaleImage, thresholdedImage};
      String title = "Global thresholding by Jason Altschuler";
      ImageViewer.showImages(toShow, title, 2, 2);
   }
}
