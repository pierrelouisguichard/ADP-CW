package adp.tsp;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * An extension of JPanel that displays an image 
 * and also tracks the number of times the image 
 * is painted for debugging purposes. 
 */
public class ImagePanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private BufferedImage image;
  
  int paintCalls = 0;
  
  public BufferedImage getImage() {
    return this.image;
  }

  public void setImage(final BufferedImage image) {
    this.image = image;
  }
  
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(this.image.getWidth(), this.image.getHeight());
  }
  
  @Override
  public void paintComponent(final Graphics g) {
    if (this.image != null) {
      g.drawImage(this.image, 0, 0, getWidth(), getHeight(), this);
      this.paintCalls++;
    }
  }

  public int paintCalls() {
    return this.paintCalls;
  }

  public void resetPaintCallCounter() {
    this.paintCalls = 0;
  }
}
