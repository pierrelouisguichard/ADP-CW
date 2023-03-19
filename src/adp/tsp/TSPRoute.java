package adp.tsp;

import java.awt.Point;
import java.util.Arrays;
import java.util.Objects;

/**
 * A class that encapsulates a specific route around the cities and the total 
 * distance that route consists of. Instances are comparable (thus sortable)
 * on the basis of this distance.
 */
public class TSPRoute implements Comparable<TSPRoute> {

  private double distance;
  private final Point[] locations;
  
  public TSPRoute(final int[] indexes, final Point[] locations) {
    // copy locations in order of indexes
    this.locations = new Point[locations.length];
    for (int i = 0; i < indexes.length; i++) {
      this.locations[i] = locations[indexes[i]];
    }  
    // compute the distance:
    this.distance = 0;
    for (int i = 1; i < indexes.length; i++) {
      this.distance += this.locations[i].distance(this.locations[i-1]);
    }
    this.distance += this.locations[0].distance(this.locations[this.locations.length-1]);
  }

  public double distance() { return this.distance; }
  public Point[] route() { return this.locations; }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + Arrays.hashCode(this.locations);
    result = (prime * result) + Objects.hash(this.distance);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    final TSPRoute other = (TSPRoute) obj;
    return (Double.doubleToLongBits(this.distance) == Double.doubleToLongBits(other.distance))
        && Arrays.equals(this.locations, other.locations);
  }

  @Override
  public int compareTo(final TSPRoute o) {
    if (this.distance > o.distance) {
      return -1;
    } else if (this.distance < o.distance) {
      return 1;
    } else {
      return 0;
    }
  }
}
