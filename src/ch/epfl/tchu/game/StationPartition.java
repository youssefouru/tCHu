package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


/**
 * A StationPartition
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] links;

    /**
     * Constructor of Station Partitions
     *
     * @param tab (int[]) : representations of the links of the
     */
    private StationPartition(int[] tab) {
        this.links = tab.clone();
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
        if (station1.id() >= links.length || station2.id() >= links.length){
            return station1.id() == station2.id();
        }
        return links[station1.id()] == links[station2.id()];
    }

    /**
     * Inner Class Builder of StationPartition
     */
    public final static class Builder {
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
            this.stationSet = new int[stationCount];
            for (int i = 0; i < stationCount; ++i) {
                stationSet[i] = i;
            }

        }


        /**
         * this method returns the representative of a set of stations
         *
         * @param stationID (int): the id of the station
         * @return returns the representative of a set of stations
         */
        private int representative(int stationID) {
            if (stationID == stationSet[stationID]) {
                return stationID;
            } else {
                return representative(stationSet[stationID]);
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
            stationSet[representative(station1.id())] = stationSet[representative(station2.id())];
            return this;
        }


        /**
         * this methods builds a StationPartition
         *
         * @return a new StationPartition
         */
        public StationPartition build() {
            for (int i = 0; i < stationSet.length; ++i) {
                stationSet[i] = representative(i);
            }

            return new StationPartition(stationSet);
        }

    }
}
