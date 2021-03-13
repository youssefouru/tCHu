package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import com.sun.source.tree.Tree;
import org.w3c.dom.Node;

import java.util.*;

public final class StationPartition implements StationConnectivity {
    private final int[][] links;

    /**
     * Constructor of Station Partitions
     *
     * @param tab (int[][]) : representations of the links of the
     */
    private StationPartition(int[][] tab) {
        this.links = tab;
    }

    /**
     * this method returns the representative of a set of stations
     *
     * @param stationID (int): the id of the station
     * @param links (int[]) : the array that contains the representative of each station
     * @return returns the representative of a set of stations
     */
    private  int representative(int stationID ,int[] links) {
        if(stationID == links[stationID]){
            return stationID;
        }else{
            return representative(links[stationID],links);
        }
    }


    /**
     * This method checks if the two stations are connected or not
     *
     * @param station1 (Station) : the first station
     * @param station2 (Station) : the second station
     * @return if the two station are connected
     */
    @Override
    public boolean connected(Station station1, Station station2) {
        return links[station1.id()][1] == links[station2.id()][1];
    }

    public final class Builder {
        private final int stationCount;

        private final int[] stationSet;





        /**
         * Constructor of the Builder
         *
         * @param stationCount (int) : the maximum ID
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.stationCount = stationCount;
            this.stationSet= new int[stationCount];
            for(int i = 0 ; i< stationCount; ++i){
                stationSet[i] = i;
            }

        }





        /**
         * this method creates a builder where the two station in parameter are connected
         *
         * @param station1 (Station) : the first station
         * @param station2 (Station) :  the second station
         * @return the builder with the two stations connected
         */
        public Builder connect(Station station1, Station station2) {
            stationSet[representative(station1.id(),stationSet)] = stationSet[representative(station2.id(),stationSet)];
            return this;
        }


        /**
         * this methods builds a StationPartition
         *
         * @return a new StationPartition
         */
        public StationPartition build() {
            for(int i = 0 ; i<stationSet.length;++i){
                stationSet[i] = representative(i,stationSet);
            }
            int[][] links = new int[stationCount][stationCount];
            for(int i = 0  ;i <stationCount ; ++i){
                links[0][i] = i;
            }
            links[1] = stationSet;
            return new StationPartition(links);
        }

    }
}
