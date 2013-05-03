package edu.vanderbilt.vm.guide.container;

import java.util.Arrays;

/**
 * POJO to represent a vertex on the map for graph algorithms.  Getters
 * and setters not used to reduce overhead.
 * @author nicholasking
 *
 */
public class MapVertex {
    
        public int[] neighbors;
        public double lat;
        public double lon;
        public int id;
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MapVertex)) return false;
            MapVertex mv = (MapVertex) o;
            if (id != mv.id) return false;
            if (lat != mv.lat) return false;
            if (lon != mv.lon) return false;
            return Arrays.equals(neighbors, mv.neighbors);
        }
        
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id;
            long n = Double.doubleToLongBits(lat);
            int c = (int)(n ^ (n >>> 32));
            result = 31 * result + c;
            n = Double.doubleToLongBits(lon);
            c = (int)(n ^ (n >>> 32));
            result = 31 * result + c;
            result = 31 * result + Arrays.hashCode(neighbors);
            return result;
        }

}
