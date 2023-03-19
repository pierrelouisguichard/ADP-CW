package adp.tsp;

/**
 * An implementation of TSPListener that is used by TSPUi to listen to 
 * an instance of TSP and forward updates to the TSPUi methods that display the routes.
 * It also records the number of update calls that are made, to compare to the
 * number of paint calls that ImagePanel reports (they should be the same).
 */
public class UIListener implements TSPListener {

  private final TSPUi ui;
  
  int updateCalls = 0;
  
  public UIListener(final TSPUi ui) {
    this.ui = ui;
  }
  
  @Override
  public void displayUpdate(final TSPRoute testRoute, final TSPRoute bestRoute) {
    this.ui.displayRouteUpdate(testRoute, bestRoute);
    this.updateCalls++;
  }

  @Override
  public void displayBest(final TSPRoute bestRoute) {
    this.ui.displayBestRoute(bestRoute); 
    System.out.println( "Update calls received: " + this.updateCalls);
  }

}
