/******************************************************************************
 * @author Jason Altschuler
 *
 * @tags image thresholding, image analysis, computer vision, machine learning
 *
 * Abstract parent class for global thresholding algorithms.
 *
 * For full documentation, see README
 *****************************************************************************/

package threshold;

public abstract class GlobalThresholding {
   
   /***********************************************************************
    * Static Fields
    **********************************************************************/
   
   // pixel intensities range from 0 to 255, inclusive, in BufferedImages
   protected final static int RADIX = 256;


   /***********************************************************************
    * Non-static fields
    **********************************************************************/
   
   // Foreground pixels >= threshold; background pixels < threshold
   protected int threshold;


   /***********************************************************************
    * Abstract methods to implement
    **********************************************************************/
   
   /**
    * Calculate appropriate global threshold given pixel array.
    * @param pixels
    */
   protected abstract void threshold(int[][] pixels);

   /***********************************************************************
    * Accessors
    **********************************************************************/
   
   /**
    * @return global threshold
    */
   public int getThreshold() {
      return threshold;
   }
   
}
