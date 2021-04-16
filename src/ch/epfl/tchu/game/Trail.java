package ch.epfl.tchu.game;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param  s1 (Station) : the first station of the trail
     * @param  s2 (Station) : the second station of the trail
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

    private static List<Trail> trailCreation(List<Route> routes){
        List<Trail> trails = new ArrayList<>();
        for (Route route: routes) {
            trails.add(new Trail(List.of(route),route.station1(),route.station2()));
            trails.add(new Trail(List.of(route),route.station2(),route.station1()));
        }
        return trails;
    }
    /**
     * Return the longest that can be created from a given list of routes
     *
     * @param routes (List<Route>) : the route list we’re going to use
     * @return  (Trail) : the longest trail that can be created from those routes
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty())
            return new Trail(new ArrayList<>(), null, null);
        List<Trail> tempTrails = new ArrayList<>();
        //we create a list of trails composed of each route that we want to check what is the is the longest trail
        List<Trail> trailsToBeTested = trailCreation(routes);
        Trail saved = new Trail(new ArrayList<>(), null, null);
        while (!trailsToBeTested.isEmpty()) {
            for (Trail trail : trailsToBeTested) {
                if (trail.length() == 0)
                    continue;
                List<Route> routesToTest = new ArrayList<>(routes);
                routesToTest.removeAll(trail.routesOfTheTrail);
                for (Route route : routesToTest) {
                    List<Station> stations = route.stations();
                    if (stations.contains(trail.station2)) {
                        tempTrails.add(trail.addARouteToTheRight(route, trail.station2));
                    }
                }
                if ( saved.length() < trail.length()) {
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
     * @return (Trail) the trail considered modified, with a route added to the right
     */
    private Trail addARouteToTheRight(Route route, Station commonStation) { //for this class to be static, a copy of routesOfTheTrailIsMade
        List<Route> myRoutes = new ArrayList<>(routesOfTheTrail);
        myRoutes.add(route);
        return new Trail(myRoutes, this.station1, route.stationOpposite(commonStation));
    }

    /**
     * Give the length of the trail on which it is use on
     *
     * @return (int) : length of the trail
     */
    public int length() {
        return length;
    }

    /**
     * Return the first station from a given trail ( the departure or not, what matters is that it's not the same extreme location than station2
     *
     * @return (Station) : A station at the end of the trail
     */
    public Station station1() {
        return station1;
    }

    /**
     * the complementary extreme location to station1
     *
     * @return  (Station) : the other extreme Station
     */
    public Station station2() {
        return station2;
    }

    /**
     * this method returns a textual representation of the trail
     *
     * @return  (String) : the textual representation of the trail
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
