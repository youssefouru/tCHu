package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public final class Trail {
    private final List<Route> routesOfTheTrail;

    private Trail(List<Route> routesOfTheTrail) {
        this.routesOfTheTrail = routesOfTheTrail;
    }


    private static Trail addARoute(Trail trail, Route route) { //for this class to be static, a copy of routesOfTheTrailIsMade
        Trail newTrail = new Trail(trail.routesOfTheTrail);
        newTrail.routesOfTheTrail.add(route);

        return newTrail;
    }

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
                extremeStation.add(Route.findCommonStation(routeBegin, routeAfterBegin));
                extremeStation.add(Route.findCommonStation(routeEnd, routeBeforeEnd));
            }
        }

        return extremeStation;
    }

    private static List<Trail> trivialTrailCreation(List<Route> routes) {
        List<Trail> trails = new ArrayList<Trail>();
        for (Route route : routes) {
            ArrayList<Route> trivialList = new ArrayList<>();
            trivialList.add(route);
            trails.add(new Trail(trivialList));
        }

        return trails;
    }

    public static Trail longest(List<Route> routes) {
        List<Trail> trailsToBeTested = trivialTrailCreation(routes);
        List<Trail> maximalTrail = new ArrayList<>();

        List<Trail> tempTrails = new ArrayList<>();
        while (!trailsToBeTested.isEmpty()) {
            for (Trail trail : trailsToBeTested) {
                Boolean canBeContinued = false;
                List<Route> routesOfThisTrail = trail.routesOfTheTrail;
                List<Station> extremeStation = extremeStationOfTheTrail(trail);
                routes.removeAll(routesOfThisTrail);

                for (Route route : routes) {
                    if (route.stations().contains(extremeStation.get(0))) {
                        tempTrails.add(addARoute(trail, route));
                        canBeContinued = true;
                    }
                    if (route.stations().contains(extremeStation.get(1))) {
                        tempTrails.add(addARoute(trail, route));
                        canBeContinued = true;
                    }
                }
                if (canBeContinued == false) {
                    maximalTrail.add(trail);
                }
            }
            trailsToBeTested = tempTrails;
            tempTrails.clear();
        }

        return longestTrailOfAList(maximalTrail);

    }

    private static Trail longestTrailOfAList(List<Trail> trails) {
        int maxLength = 0;
        Trail longestTrail = new Trail(null);

        for (Trail trail : trails) {
            int length = lengthStatic(trail);
            if (length > maxLength) {
                maxLength = length;
                longestTrail = trail;
            }
        }

        return longestTrail;

    }


    private static int lengthStatic(Trail trail) {
        int length = 0;
        List<Route> routes = trail.routesOfTheTrail;
        for (Route route : routes) {
            length += route.length();
        }
        return length;
    }

    public int length() {
       return lengthStatic(this);
    }

    public Station station1() {
        List<Route> routes = this.routesOfTheTrail;
        Station toBeReturned;
        if (routes.size() == 0) {
            toBeReturned = null;
        }
        toBeReturned= extremeStationOfTheTrail(this).get(0);

        return toBeReturned;
    }

    public Station station2() {
        List<Route> routes = this.routesOfTheTrail;
        Station toBeReturned;
        if (routes.size() == 0) {
            toBeReturned = null;
        }
        toBeReturned= extremeStationOfTheTrail(this).get(1);

        return toBeReturned;
    }


}
