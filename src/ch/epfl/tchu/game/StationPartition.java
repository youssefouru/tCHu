package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


/**
 * A StationPartition : this class is a representation of the connection between the stations
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
     * @return (boolean) : that station 1 and 2 are connected or not
     */
    @Override
    public boolean connected(Station station1, Station station2) {
        if (station1.id() >= links.length || station2.id() >= links.length) {
            return station1.id() == station2.id();
        }
        return links[station1.id()] == links[station2.id()];
    }

    /**
     * Inner Class Builder of StationPartition : this is a the builder of the StationPartition class that will help us to connect the stations
     */
    public final static class Builder {
        private final int[] stationSet;

        /**
         * Constructor of the Builder
         *
         * @param stationCount (int) : the maximum ID
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
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
            }
            return representative(stationSet[stationID]);

        }


        /**
         * this method returns this builder where we connected the two station in parameter
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
