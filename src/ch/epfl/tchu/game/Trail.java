package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.List;

/**
 * Trail : this class represents a trail
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
     * @param routesOfTheTrail (List<Route>) : the route list of each
     */
    private Trail(List<Route> routesOfTheTrail, Station s1, Station s2) {
        this.routesOfTheTrail = List.copyOf(routesOfTheTrail);
        this.length = computeLength(routesOfTheTrail);
        station1 = s1;
        station2 = s2;

    }

    private static int computeLength(List<Route> routes) {
        int i = 0;
        for (Route route : routes) {
            i += route.length();
        }
        return i;
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
            trails.add(new Trail(List.of(route), route.station1(), route.station2()));
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
            return new Trail(new ArrayList<>(), null, null);
        List<Trail> tempTrails = new ArrayList<>();
        List<Trail> trailsToBeTested = trivialTrailCreation(routes);
        Trail saved = new Trail(new ArrayList<>(), null, null);
        while (!trailsToBeTested.isEmpty()) {
            for (Trail trail : trailsToBeTested) {
                if (trail.length() == 0)
                    continue;
                //this boolean will determine if the trail can be continued or not that will help us when we want to take the maximum trail between the ones that only can not be continued
                boolean canBeContinued = false;
                List<Route> routesToTest = new ArrayList<>(routes);
                routesToTest.removeAll(trail.routesOfTheTrail);
                for (Route route : routesToTest) {
                    List<Station> stations = route.stations();
                    if (stations.contains(trail.station2)) {
                        tempTrails.add(trail.addARouteToTheRight(route, trail.station2));
                        canBeContinued = true;
                    } else if (stations.contains(trail.station1)) {
                        tempTrails.add(trail.addARouteToTheLeft(route, trail.station1));
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
    private Trail addARouteToTheRight(Route route, Station commonStation) { //for this class to be static, a copy of routesOfTheTrailIsMade
        List<Route> myRoutes = new ArrayList<>(routesOfTheTrail);
        myRoutes.add(route);
        return new Trail(myRoutes, this.station1, route.stationOpposite(commonStation));
    }

    /**
     * add a route to the left of the list of routes of the train
     *
     * @param route (Route) the route that must be added
     * @return the trail considered modified, with a route added to the left
     */
    private Trail addARouteToTheLeft(Route route, Station commonStation) {
        List<Route> myRoutes = new ArrayList<>(routesOfTheTrail);
        myRoutes.add(route);
        return new Trail(myRoutes, route.stationOpposite(commonStation), this.station2);
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
