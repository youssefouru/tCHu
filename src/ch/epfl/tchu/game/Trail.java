package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Trail : this class represents a trail
 *
 * @author Amine Youssef (324253)
 * @author Louis Yves André Barinka (329847)
 */
public final class Trail {
    private final static Trail emptyTrail = new Trail(new ArrayList<>(), null, null, 0);
    private final List<Route> routesOfTheTrail;
    private final int length;
    private final Station station1, station2;

    /**
     * Constructor of Trail
     *
     * @param routesOfTheTrail (List<Route>) : the route list of each
     * @param station1         (Station) : the first station of the trail
     * @param station2         (Station) : the second station of the trail
     */
    private Trail(List<Route> routesOfTheTrail, Station station1, Station station2, int length) {
        this.routesOfTheTrail = List.copyOf(routesOfTheTrail);
        this.length = length;
        this.station1 = station1;
        this.station2 = station2;

    }

    private static List<Trail> trailCreation(List<Route> routes) {
        List<Trail> trails = new ArrayList<>();
        for (Route route : routes) {
            trails.add(new Trail(List.of(route), route.station1(), route.station2(), route.length()));
            trails.add(new Trail(List.of(route), route.station2(), route.station1(), route.length()));
        }
        return trails;
    }


    /**
     * Return the longest that can be created from a given list of routeOwner
     *
     * @param routes (List<Route>) : the route list we’re going to use
     * @return (Trail) : the longest trail that can be created from those routeOwner
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty())
            return emptyTrail;
        List<Trail> tempTrails = new ArrayList<>();
        //we create a list of trails composed of each route that we want to check what is the is the longest trail
        List<Trail> trailsToBeTested = trailCreation(routes);
        Trail saved = new Trail(new ArrayList<>(), null, null, 0);

        while (!trailsToBeTested.isEmpty()) {

            for (Trail trail : trailsToBeTested) {

                List<Route> routesToTest = new ArrayList<>(routes);

                routesToTest.removeAll(trail.routesOfTheTrail);

                for (Route route : routesToTest) {
                    List<Station> stations = route.stations();
                    if (stations.contains(trail.station2)) {
                        tempTrails.add(trail.addARouteToTheRight(route, trail.station2));
                    }
                }
                if (saved.length() < trail.length()) {
                    saved = trail;
                }

            }

            trailsToBeTested = new ArrayList<>(tempTrails);
            tempTrails.clear();
        }
        return saved;
    }


    /**
     * add a route to the right of the list of routeOwner of the train
     *
     * @param route (Route) the route that must be added
     * @return (Trail) the trail considered modified, with a route added to the right
     */
    private Trail addARouteToTheRight(Route route, Station commonStation) {
        List<Route> myRoutes = new ArrayList<>(routesOfTheTrail);
        myRoutes.add(route);
        return new Trail(myRoutes, this.station1, route.stationOpposite(commonStation), length + route.length());
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
     * @return (Station) : the other extreme Station
     */
    public Station station2() {
        return station2;
    }

    /**
     * This method returns the routes of the trail
     *
     * @return (List < Route >) : the attribute route
     */
    public List<Route> routes() {
        return routesOfTheTrail;
    }

    /**
     * this method returns a textual representation of the trail
     *
     * @return (String) : the textual representation of the trail
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
