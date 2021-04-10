package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A Trail
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Trail {
    private final List<Route> routesOfTheTrail;
    private final int length;
    private final Station station1;
    private final Station station2;


    /**
     * Constructor of Trail
     *
     * @param routesOfTheTrail (List<Route>) : the route list that we will use
     */
    private Trail(List<Route> routesOfTheTrail) {
        this.routesOfTheTrail = List.copyOf(routesOfTheTrail);
        this.length = computeLength(routesOfTheTrail);
        List<Station> extremeStations = extremeStationOfTheTrail(routesOfTheTrail);
        if (extremeStations.isEmpty()) {
            station1 = null;
            station2 = null;
        } else {
            station1 = extremeStations.get(0);
            station2 = extremeStations.get(1);
        }
    }

    private static int computeLength(List<Route> routes) {
        int i = 0;
        for (Route route : routes) {
            i += route.length();
        }
        return i;
    }

    /**
     * find the common Station between the route 1 and 2
     *
     * @param route1 (Route) : the first Route
     * @param route2 (Route) : the second Route
     * @return the common Station of the route 1 and 2
     */
    private static Station findCommonStation(Route route1, Route route2) {
        List<Station> route2Stations = route2.stations();
        if (route2Stations.contains(route1.station1())) {
            return route1.station1();
        } else if (route2Stations.contains(route1.station2())) {
            return route1.station2();
        }
        return null;
    }

    /**
     * Creates a List of trail that are only composed of one different route
     *
     * @param routes (List<Route>) : the basic routes that serve the trivialTrailCreation
     * @return trails (Trail) : A list of trail that are only composed of one road
     */
    private static List<Trail> trivialTrailCreation(List<Route> routes) {
        List<Trail> trails = new ArrayList<>();
        for (Route route : routes) {
            trails.add(new Trail(List.of(route)));
        }
        return trails;
    }

    /**
     * Return the longest that can be created from a given list of routes
     *
     * @param routes (List<Route>) : the route list we’re going to use
     * @return the longest trail that can be created from those routes
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty())
            return new Trail(new ArrayList<>());
        List<Trail> tempTrails = new ArrayList<>();
        List<Trail> trailsToBeTested = trivialTrailCreation(routes);
        Trail saved = new Trail(new ArrayList<>());
        while (!trailsToBeTested.isEmpty()) {
            for (Trail trail : trailsToBeTested) {
                if (trail.length() == 0)
                    continue;
                boolean canBeContinued = false;
                List<Route> routesToTest = new ArrayList<>(routes);
                routesToTest.removeAll(trail.routesOfTheTrail);
                for (Route route : routesToTest) {
                    List<Station> stations = route.stations();
                    if (stations.contains(trail.station2)) {
                        tempTrails.add(trail.addARouteToTheRight(route));
                        canBeContinued = true;
                    } else if (stations.contains(trail.station1)) {
                        tempTrails.add(trail.addARouteToTheLeft(route));
                        canBeContinued = true;
                    }
                }
                if (!canBeContinued && saved.length() < trail.length()) {
                    saved = trail;
                }

            }

            trailsToBeTested = new ArrayList<>(tempTrails);
            tempTrails.clear();
        }
        return saved;
    }

    /**
     * add a route to the right of the list of routes of the train
     *
     * @param route (Route) the route that must be added
     * @return the trail considered modified, with a route added to the right
     */
    private Trail addARouteToTheRight(Route route) { //for this class to be static, a copy of routesOfTheTrailIsMade
        List<Route> myRoutes = new ArrayList<>(routesOfTheTrail);
        myRoutes.add(route);
        return new Trail(myRoutes);
    }

    /**
     * add a route to the left of the list of routes of the train
     *
     * @param route (Route) the route that must be added
     * @return the trail considered modified, with a route added to the left
     */
    private Trail addARouteToTheLeft(Route route) {
        List<Route> myRoutes = new LinkedList<>(routesOfTheTrail);
        myRoutes.add(0, route);
        return new Trail(myRoutes);
    }

    /**
     * Return the two extreme stations of this trail
     *
     * @param routes (List<Route>): the list of routes of the trail that we want to determine the extreme stations
     * @return extremeStation (List<Station>) : the list of the two extreme station
     */
    private static List<Station> extremeStationOfTheTrail(List<Route> routes) {
        int routesSize = routes.size();
        if (routesSize == 0)
            return new ArrayList<>();
        if (routesSize == 1)
            return routes.get(0).stations();
        List<Station> extremeStation = new ArrayList<>();
        Route routeBegin = routes.get(0);
        Route routeEnd = routes.get(routesSize - 1);
        Route routeAfterBegin = routes.get(1);
        Route routeBeforeEnd = routes.get(routesSize - 2);

        // Now we identify the common station
        extremeStation.add(routeBegin.stationOpposite(findCommonStation(routeBegin, routeAfterBegin)));
        extremeStation.add(routeEnd.stationOpposite(findCommonStation(routeEnd, routeBeforeEnd)));
        return extremeStation;
    }

    /**
     * Give the length of the trail on which it is use on
     *
     * @return length of the trail
     */
    public int length() {
        return length;
    }

    /**
     * Return the first station from a given trail ( the departure or not, what matters is that it's not the same extreme location than station2
     *
     * @return A station at the end of the trail
     */
    public Station station1() {
        return station1;
    }

    /**
     * the complementary extreme location to station1
     *
     * @return the other extreme Station
     */
    public Station station2() {
        return station2;

    }

    /**
     * this method returns a textual representation of the trail
     *
     * @return the textual representation of the trail
     */
    @Override
    public String toString() {
        String s1, s2;
        if (length() == 0) {
            s1 = "-----";
            s2 = "-----";
        } else {
            s1 = station1().toString();
            s2 = station2().toString();
        }
        return String.format("%s - %s (%s)", s1, s2, length);
    }

}
