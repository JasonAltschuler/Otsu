/**
 * @author Jason Altschuler
 * 
 * Provides an easy way to display BufferedImages using javax.swing
 **/

package ui;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class ImageViewer extends JFrame {
   private static final long serialVersionUID = -3399099878628444590L; // LOL "magic"

   /***********************************************************************
    * Interface
    ***********************************************************************/
   
   /**
    * Displays images side by side.
    * @param images
    */
   public static void showImages(BufferedImage[] images, String title) {
      ImageViewer ui = new ImageViewer(images);
      makePretty(ui, title);
   }
   
   
   /**
    * Displays images in a grid layout. 
    * @param images
    * @param title
    * @param rows
    * @param columns
    */
   public static void showImages(BufferedImage[] images, String title, int rows, int columns) {
      checkGridDimensions(images.length, rows, columns);
      ImageViewer ui = new ImageViewer(images, rows, columns);
      makePretty(ui, title);
   }
   
   
   /**
    * Displays images side by side. 
    * @param files
    * @param title
    */
   public static void showImages(String[] files, String title) {
      ImageViewer ui = new ImageViewer(files);
      makePretty(ui, title);
   }
   
   
   /**
    * Displays images in a grid layout. 
    * @param files
    * @param title
    * @param rows
    * @param columns
    */
   public static void showImages(String[] files, String title, int rows, int columns) {
      checkGridDimensions(files.length, rows, columns);
      ImageViewer ui = new ImageViewer(files, rows, columns);
      makePretty(ui, title);
   }
   
   
   /***********************************************************************
    * Static helper methods for showing images
    ***********************************************************************/
   
   /**
    * Makes the UI super pretty.
    * @param ui
    */
   private static void makePretty(ImageViewer ui, String title) {
      ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      ui.pack();
      ui.setTitle(title);
      ui.setVisible(true);
   }
   
   
   /**
    * Checks that the grid layout is big enough to display all images. 
    * @param n
    * @param rows
    * @param columns
    */
   private static void checkGridDimensions(int n, int rows, int columns) {
      if (n > rows * columns)
         throw new IllegalArgumentException("layout has insufficient size");
   }

   
   /***********************************************************************
    * Create and populate the JFrame
    ***********************************************************************/
   
   /**
    * Displays images side by side.
    * @param images
    */
   private ImageViewer(BufferedImage[] images) {
      setLayout(new GridLayout(1, images.length));
      addImages(images);
   }

   
   /**
    * Displays images in a grid layout.
    * @param images
    * @param rows
    * @param columns
    */
   private ImageViewer(BufferedImage[] images, int rows, int columns) {
      setLayout(new GridLayout(rows, columns));
      addImages(images);
   }
   
   
   /**
    * Displays images side by side.
    * @param files
    */
   private ImageViewer(String[] files) {
      setLayout(new GridLayout(1, files.length));
      addImages(files);
   }
   

   /**
    * Displays images in a grid layout.
    * @param files
    * @param rows
    * @param columns
    */
   private ImageViewer(String[] files, int rows, int columns) {
      setLayout(new GridLayout(rows, columns));
      addImages(files);      
   }
   
   
   /***********************************************************************
    * Non-static helper methods for ImageViewer
    ***********************************************************************/
   
   /**
    * Populates the JFrame with images.
    * @param images
    */
   private void addImages(BufferedImage[] images) {
      for (int i = 0; i < images.length; i++)
         add(new JLabel(new ImageIcon(images[i])));
   }
   
   
   /**
    * TODO: check works
    * Populates the JFrame with images.
    * @param files
    */
   private void addImages(String[] files) {
      for (int i = 0; i < files.length; i++) {
         try {
            add(new JLabel(new ImageIcon(ImageIO.read(new File(files[i])))));
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

}
