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

    /**
     * Constructor of Trail
     *
     * @param routesOfTheTrail (List<Route>) : the route list that we will use
     */
    private Trail(List<Route> routesOfTheTrail) {
        this.routesOfTheTrail =List.copyOf(routesOfTheTrail);
    }


    /**
     * add a route to the right of the list of routes of the train
     *
     * @param trail (Trail) the trail to which we will add a route to the right
     * @param route (Route) the route that must be added
     * @return the trail considered modified, with a route added to the right
     */
    private static Trail addARouteToTheRight(Trail trail, Route route) { //for this class to be static, a copy of routesOfTheTrailIsMade
        List<Route> myRoutes = new ArrayList<>(trail.routesOfTheTrail);
        myRoutes.add(route);
        Trail newTrail = new Trail(myRoutes);
        return newTrail;
    }

    /**
     * add a route to the left of the list of routes of the train
     *
     * @param trail (Trail) the trail to which we will add a route to the left
     * @param route (Route) the route that must be added
     * @return the trail considered modified, with a route added to the left
     */
    private static Trail addARouteToTheLeft(Trail trail, Route route) {
        List<Route> myRoutes = new LinkedList<>(trail.routesOfTheTrail);
        myRoutes.add(0,route);
        Trail newTrail = new Trail(myRoutes);
        return newTrail;
    }

    /**
     * Return the two extreme stations of a trail in a list
     *
     * @param trail (Trail) : the Trail  we’re going to use
     * @return extremeStation, the list of the two extreme station
     */
    private static List<Station> extremeStationOfTheTrail(Trail trail) {
        List<Station> extremeStation = new ArrayList<>();

        if (trail.routesOfTheTrail.size() > 0) {
            if (trail.routesOfTheTrail.size() == 1) {
                extremeStation = trail.routesOfTheTrail.get(0).stations();
            } else {
                Route routeBegin = trail.routesOfTheTrail.get(0);
                Route routeEnd = trail.routesOfTheTrail.get(trail.routesOfTheTrail.size() - 1);
                Route routeAfterBegin = trail.routesOfTheTrail.get(1);
                Route routeBeforeEnd = trail.routesOfTheTrail.get(trail.routesOfTheTrail.size() - 2);

                // Now we identify the common station
                extremeStation.add(routeBegin.stationOpposite(Route.findCommonStation(routeBegin, routeAfterBegin)));
                extremeStation.add(routeEnd.stationOpposite(Route.findCommonStation(routeEnd, routeBeforeEnd)));
            }
        }

        return extremeStation;
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
            ArrayList<Route> trivialList = new ArrayList<>();
            trivialList.add(route);
            trails.add(new Trail(trivialList));
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
        List<Trail> trailsToBeTested = trivialTrailCreation(routes);
        List<Trail> maximalTrail = new ArrayList<>();
        Trail toBeReturned = null;

        if (routes.size() != 0) {
            List<Trail> tempTrails = new ArrayList<>();
            while (!trailsToBeTested.isEmpty()) {
                for (Trail trail : trailsToBeTested) {
                    List<Route> copy = new ArrayList<>(trail.routesOfTheTrail);
                    boolean canBeContinued = false;
                    List<Route> routesOfThisTrail = copy;
                    List<Station> extremeStation = extremeStationOfTheTrail(trail);
                    List<Route> routesToTest = new ArrayList<>(routes);
                    routesToTest.removeAll(routesOfThisTrail);

                    for (Route route : routesToTest) {
                        if (route.stations().contains(extremeStation.get(1))) {
                            tempTrails.add(addARouteToTheRight(trail, route));
                            canBeContinued = true;
                        }if(route.stations().contains(extremeStation.get(0))){
                            tempTrails.add(addARouteToTheLeft(trail, route));
                            canBeContinued = true;
                        }
                    }
                    if (!canBeContinued) {
                        maximalTrail.add(trail);
                    }
                }
                trailsToBeTested = new ArrayList<>(tempTrails);
                tempTrails.clear();
            }
            toBeReturned = longestTrailOfAList(maximalTrail);
        }
        if (routes.size() == 0) {
            List<Route> trivialList = new ArrayList<>();
            Trail trivialTrail = new Trail(trivialList);
            toBeReturned = trivialTrail;
        }

        return toBeReturned;
    }

    /**
     * Given a list of trails, return the longest trail of them all (by adding the distance of each route)
     *
     * @param trails (Trail) : the Trail list we’re going to use
     * @return Trail the longest trails of them all
     */
    private static Trail longestTrailOfAList(List<Trail> trails) {
        int maxLength = 0;
        Trail longestTrail = new Trail(new ArrayList<>());

        for (Trail trail : trails) {
            int length = lengthStatic(trail);
            if (length > maxLength) {
                maxLength = length;
                longestTrail = trail;
            }
        }
        return longestTrail;

    }

    /**
     * Return the length of a given trail (the sum of the distance of its routes) and is static
     *
     * @param trail (Trail) :the Trail  we’re going to use
     * @return length
     */
    private static int lengthStatic(Trail trail) {
        int length = 0;
        List<Route> routes = trail.routesOfTheTrail;
        for (Route route : routes) {
            length += route.length();
        }
        return length;
    }

    /**
     * Give the length of the trail on which it is use on
     *
     * @return length of the trail
     */
    public int length() {
        return lengthStatic(this);
    }

    /**
     * Return the first station from a given trail ( the departure or not, what matters is that it's not the same extreme location than station2
     *
     * @return A station at the end of the trail
     */
    public Station station1() {
        if (routesOfTheTrail.size() == 0) {
           return null;
        } else {
            return extremeStationOfTheTrail(this).get(0);
        }
    }

    /**
     * the complementary extreme location to station1
     *
     * @return the other extreme Station
     */
    public Station station2() {
        if (routesOfTheTrail.size() == 0) {
            return null;
        } else {
            return extremeStationOfTheTrail(this).get(1);
        }
    }

    /**
     * this method returns a textual representation of the trail
     *
     * @return the textual representation of the trail
     */
    @Override
    public String toString() {
        int trailLength = lengthStatic(this);
        String s1,s2;
        if(routesOfTheTrail.size() == 0){
            s1 = "-----";
            s2 = "-----";
        }else{
            s1 = station1().toString();
            s2 = station2().toString();
        }
        String result = String.format("%s - %s (%s)", s1, s2, trailLength);
        return result;
    }

}
