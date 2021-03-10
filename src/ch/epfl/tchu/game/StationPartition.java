package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     * @param stations (Set<Station>) : the set of stations
     * @return returns the representative of a set of stations
     */
    private static int representative(Set<Station> stations) {
        int max = 0;
        for (Station station : stations) {
            if (station.id() >= max) {
                max = station.id();
            }
        }
        return max;
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

        private final List<Set<Station>> stationSet;

        /**
         * Constructor of the Builder
         *
         * @param stationCount (int) : the maximum ID
         */
        public Builder(int stationCount) {
            this(stationCount, new ArrayList<>());
        }

        /**
         * Constructor of the Builder
         *
         * @param stationCount (int) : the maxium ID
         * @param stationSet   (Set<Set<Station>>) : the set pf stations that are connected
         */
        private Builder(int stationCount, List<Set<Station>> stationSet) {
            Preconditions.checkArgument(stationCount >= 0);
            this.stationSet = List.copyOf(stationSet);
            this.stationCount = stationCount;
        }

        /**
         * this method creates a builder where the two station in parameter are conncted
         *
         * @param station1 (Station) : the first station
         * @param station2 (Station) :  the second station
         * @return the builder with the two stations connected
         */
        public Builder connect(Station station1, Station station2) {
            List<Set<Station>> stations = new ArrayList<>(stationSet);

            for (Set<Station> station : stations) {
                if (station.contains(station1) && !station.contains(station2)) {
                    station.add(station2);
                    return new Builder(stationCount, stations);
                }
                if (station.contains(station2) && !station.contains(station1)) {
                    station.add(station1);
                    return new Builder(stationCount, stations);
                }

            }
            stations.add(Set.of(station1, station2));
            return new Builder(stationCount, stationSet);
        }


        /**
         * this methods builds a StationPartition
         *
         * @return a new StationPartition
         */
        public StationPartition build() {
            int[][] links = new int[stationCount][2];
            for (Set<Station> stationSet : this.stationSet) {
                for (Station station : stationSet) {
                    links[station.id()][1] = representative(stationSet);
                }
            }

            return new StationPartition(links);
        }

    }
}
