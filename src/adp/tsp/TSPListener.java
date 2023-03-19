package adp.tsp;

/**
 * An interface for objects that want to listen to the TSP class output.
 * There are two methods, one called for each route tested, and one called 
 * at the end with the best route found.
 */
public interface TSPListener {  
  void displayUpdate(TSPRoute testRoute, TSPRoute bestRoute);
  void displayBest(TSPRoute bestRoute);
}