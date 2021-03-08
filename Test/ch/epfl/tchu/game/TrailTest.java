package ch.epfl.tchu.game;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class TrailTest {


    private static Route route1 = ChMap.routes().get(46);//GEN-LAU length = 4
    private static Route route2 = ChMap.routes().get(56);//LAU-NEU  length = 4
    private static Route route3 = ChMap.routes().get(48);//GEN-YVE length = 6
    private static Route route4 = ChMap.routes().get(66);//NEU-YVE length = 2
    private static Route route5 = ChMap.routes().get(45);//FRI-LAU length = 3

    private static Route route6 = ChMap.routes().get(25);//BRU-COI length = 5
    private static Route route7 = ChMap.routes().get(27);//BRU-IT_2 length = 2
    private static Route route8 = ChMap.routes().get(30);//COI-WAS length = 5

    private static Route route9 = ChMap.routes().get(53);//KRE-STG length = 1

    private static Route route10 = ChMap.routes().get(69);//PFA-SAR length = 3





    private static List<Route> listWith1Route = new ArrayList<>();
    private static List<Route> listWith2Routes = new ArrayList<>();
    private static List<Route> listWith10Routes = new ArrayList<>();
    private static List<Route> emptyList = new ArrayList<>();


    private static Trail trailWith1Route;
    private static Trail trailWith2Routes;
    private static Trail trailWith10Routes;
    private static Trail trailWithEmptyList;

    @BeforeAll
    public static void setUp(){
        listWith1Route.add(route1);
        listWith2Routes.add(route1);
        listWith2Routes.add(route2);
        listWith10Routes.add(route1);
        listWith10Routes.add(route2);
        listWith10Routes.add(route3);
        listWith10Routes.add(route4);
        listWith10Routes.add(route5);
        listWith10Routes.add(route6);
        listWith10Routes.add(route7);
        listWith10Routes.add(route8);
        listWith10Routes.add(route9);
        listWith10Routes.add(route10);
        /*System.out.println(listWith10Routes.size());
        trailWith1Route = Trail.longest(listWith1Route);
        trailWith2Routes = Trail.longest(listWith2Routes);
        trailWith10Routes = Trail.longest(listWith10Routes);
        trailWithEmptyList = Trail.longest(emptyList);*/


    }



    @Test
    void longestWorkWith10Routes(){
        Trail trail = Trail.longest(listWith10Routes);
        assertEquals(19,trail.length());
    }


    /*

    pas encore sûr que ça ça marche

    @Test
    void toStringWorlsWith10Routes(){
        assertEquals("Genève",trailWith10Routes.toString());
    }
*/

}