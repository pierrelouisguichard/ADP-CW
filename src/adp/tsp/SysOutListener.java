package adp.tsp;

import java.util.Arrays;

/**
 * An implementation of TSPListener that simply prints output on the command line. 
 * This is used by the TSP class' main method for demonstration purposes.
 */
public class SysOutListener implements TSPListener {

  @Override
  public void displayUpdate(final TSPRoute route, final TSPRoute bestRoute) {
    System.out.println( "TEST: " + route.distance());
    System.out.println( "BEST: " + bestRoute.distance());
  }

  @Override
  public void displayBest(final TSPRoute bestRoute) {
    System.out.println( "FINAL BEST: " + bestRoute.distance());
    System.out.println(Arrays.toString(bestRoute.route()));
  }

}
