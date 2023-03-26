package adp.tsp;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The UI application that provides the window and has a main method.
 */
public class TSPUi extends JFrame {

  private static final long serialVersionUID = 1L;

  private static int MINIMUM_NUMBER_OF_CITIES = 4;
  
  private final BufferedImage image;
  private final ImagePanel imagePanel = new ImagePanel();
  private final JComboBox<String> numberOfCitiesCombo;
  private final JButton goButton = new JButton("Go");
  private final JButton cancelButton = new JButton("Cancel");
  private final JButton replayButton = new JButton("Replay longest to shortest");
  
  private final SortedSet<TSPRoute> allRoutes = new TreeSet<>();
  private volatile boolean cancelled = false;
  private Thread goThread;
  private Thread replayThread;
  private volatile TSP tsp;

  public void cancelled() {
    this.cancelled = !cancelled;
  }

     
  public TSPUi(final int width, final int height) {

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
    this.imagePanel.setImage(this.image);
    
    final JPanel mainPanel = new JPanel(new BorderLayout());
    final JPanel topPanel = new JPanel();
    
    final String[] cities = new String[6];
    for (int i = 0; i < cities.length; i++) {
      cities[i] = i + MINIMUM_NUMBER_OF_CITIES + " cities";
    }
    this.numberOfCitiesCombo = new JComboBox<String>(cities);
    this.goButton.addActionListener((ev)->(goThread = new Thread(this::runAnimation)).start());
    this.cancelButton.addActionListener((ev)->cancel());
    this.replayButton.addActionListener((ev)->(replayThread = new Thread(this::showLongestToShortest)).start());
    this.cancelButton.setEnabled(false);
    this.replayButton.setEnabled(false);

    topPanel.add(this.numberOfCitiesCombo);
    topPanel.add(this.goButton);
    topPanel.add(this.cancelButton);
    topPanel.add(this.replayButton);

    mainPanel.add(topPanel, BorderLayout.NORTH);
    mainPanel.add(this.imagePanel, BorderLayout.CENTER);
    
    add(mainPanel);
    pack();
    setVisible(true);
    
  }

  private void runAnimation() {  
    final int numberOfCities = this.numberOfCitiesCombo.getSelectedIndex() + MINIMUM_NUMBER_OF_CITIES;
    this.goButton.setEnabled(false);
    this.replayButton.setEnabled(false);
    this.cancelButton.setEnabled(true);
    this.allRoutes.clear();
    this.imagePanel.resetPaintCallCounter();
    
    tsp = new TSP(numberOfCities, TSPUi.this.image.getWidth(), TSPUi.this.image.getHeight());
    tsp.setListener(new UIListener(TSPUi.this));
    tsp.findShortestRoute();
    
  }

  private void cancel() {
          
    System.out.println( "Cancelling...");
    this.goButton.setEnabled(true);
    this.cancelButton.setEnabled(false);
    this.replayButton.setEnabled(true);
    tsp.cancelled();
    cancelled();
    goThread.interrupt();
    replayThread.interrupt();

  }

  public void oneQueue(TSPRoute route) {
    try {
      SwingUtilities.invokeAndWait(() -> displayOneRoute(route, Color.WHITE));
    } catch (InterruptedException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  public void bestQueue(TSPRoute route) {
    try {
      SwingUtilities.invokeAndWait(() -> displayBestRoute(route));
    } catch (InterruptedException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
  
  private void showLongestToShortest() {
    this.imagePanel.resetPaintCallCounter();
    this.replayButton.setEnabled(false);
    this.cancelButton.setEnabled(true);
    this.goButton.setEnabled(false);
    this.cancelled = false;
    
    for(final TSPRoute route : TSPUi.this.allRoutes) {
      if(!cancelled) {
        oneQueue(route);
      }

    }
    if(!cancelled) {
      bestQueue(TSPUi.this.allRoutes.last());
    }
  }

  public void displayBestRoute(final TSPRoute bestRoute) {
    displayOneRoute(bestRoute, Color.GREEN);
    System.out.println("Paint calls: " + this.imagePanel.paintCalls());
    this.goButton.setEnabled(true);
    this.cancelButton.setEnabled(false);
    this.replayButton.setEnabled(true);
  }

  private void displayOneRoute(final TSPRoute bestRoute, final Color color) {
    final Graphics2D g = (Graphics2D) this.image.getGraphics();
    g.setFont(Font.decode("SANSSERIF-BOLD-24"));
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
    
    g.setColor(Color.BLACK);
    g.drawString(String.format("%.2f", bestRoute.distance()), 5, 24);

    g.setColor(color);
    g.setStroke(new BasicStroke(7f));
    paintPath(g, bestRoute);

    paintLocations(bestRoute.route(), g);

    this.imagePanel.repaint();
  }

  public void displayRouteUpdate(final TSPRoute route, final TSPRoute bestRoute) {
    this.allRoutes.add(route);
    final Graphics2D g = (Graphics2D) this.image.getGraphics();
    g.setFont(Font.decode("SANSSERIF-BOLD-24"));
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());
    
    g.setColor(Color.BLACK);
    g.drawString(String.format("%.2f (%.2f)", route.distance(), bestRoute.distance()), 5, 24);

    g.setColor(Color.WHITE); 
    g.setStroke(new BasicStroke(7f));
    paintPath(g, bestRoute);

    g.setColor(Color.DARK_GRAY);
    g.setStroke(new BasicStroke(1f));
    paintPath(g, route);

    paintLocations(route.route(), g);

    // this call to paintImmediately is used to ensure that every call to displayUpdate
    // actually gets painted - if we used the asynchronous repaint() method as is more
    // usual, paint jobs could be discarded when a new paint job arrived, resulting in
    // just the last update actually being displayed. However, this approach means that
    // thousands of repaint events are on the edt and user interactivity is blocked - so no good!
    //this.imagePanel.paintImmediately(0,0,this.imagePanel.getWidth(), this.imagePanel.getHeight());
    
    this.imagePanel.repaint();
  }

  private void paintLocations(final Point[] locations, final Graphics2D g) {
    g.setColor(Color.MAGENTA);
    for(final Point p : locations) {
      g.fillOval(p.x - 5, p.y - 5, 15, 15);
      g.setColor(Color.BLUE);
    }
  }
  
  private void paintPath(final Graphics2D g, final TSPRoute path) {
    final Point[] locations = path.route();
    for (int i = 1; i < locations.length; i++) {
      final Point s = locations[i - 1];
      final Point e = locations[i];
      g.drawLine(s.x, s.y, e.x, e.y);
    }
    final Point s = locations[locations.length - 1];
    final Point e = locations[0];
    g.drawLine(s.x, s.y, e.x, e.y);    
  }

  
  public static void launch() {
    new TSPUi(500, 500);
  }
  
  public static void main(final String[] args) {
    SwingUtilities.invokeLater(()->launch());
  }
  
}
