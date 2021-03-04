package ch.epfl.tchu.game;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTestT {
    private static Route route1 = ChMap.routes().get(46);//GEN-LAU  4
    private static Route route2 = ChMap.routes().get(56);//LAU-NEU 4
    private static Route route3 = ChMap.routes().get(48);//GEN-YVE 6
    private static Route route4 = ChMap.routes().get(66);//NEU-YVE  2

    // The length of the longest route is 16 here
    private static Route route5 = ChMap.routes().get(27);//BRU-IT_2 2
    private static Route route6 = ChMap.routes().get(30);//COI-WAS 5
    private static Route route7 = ChMap.routes().get(53);//KRE-STG 1
    private static Route route8 = ChMap.routes().get(76);//SCE-ZUR 3

    private static List<Route> trivialList = new ArrayList<>();
    private static List<Route> sizeOneList = new ArrayList<>();
    private static List<Route> longerList = new ArrayList<>();
    private static List<Route> soundListForATrail = new ArrayList<>();

    private static Trail longerTrail = null;
    private static Trail trailOfSizeOne = null;
    private static Trail trivialTrail = null;
    private static Trail trailForTestPublic = null;




    @BeforeAll
    static void setUp() {

        soundListForATrail.add(route1);
        soundListForATrail.add(route2);
        soundListForATrail.add(route4);
        soundListForATrail.add(route3);


        longerList.add(route1);
        longerList.add(route2);
        longerList.add(route3);
        longerList.add(route4);
        longerList.add(route5);
        longerList.add(route6);
        longerList.add(route7);
        longerList.add(route8);


        sizeOneList.add(route1);

         longerTrail = Trail.longest(longerList);
         trailOfSizeOne = Trail.longest(sizeOneList);
         trivialTrail = Trail.longest(trivialList);
        trailForTestPublic = new Trail(soundListForATrail);

    }



    @Test

    void longestWorkWithTrivialList() {
        assertEquals(0, Trail.longest(trivialList).length());
    }

    @Test
    void longestWorkWithLongerList() {
       Trail trail= Trail.longest(longerList);
       assertEquals(16, Trail.longest(longerList).length());

    }

    @Test
    void lengthWorkWithTrailOfSizeOne() {
        assertEquals(4, trailOfSizeOne.length());
    }


    @Test
    void lengthWorkWithLongerTrail() {
        assertEquals(16, longerTrail);
    }

    @Test
    void station1WorkWithTrail() {
        assertEquals(route1.station2(), trailForTestPublic.station1());
    }

   @Test
    void station1WorkWithOneTrail() {
        assertEquals(trailOfSizeOne.station1(),route1.station1());
    }

    @Test
    void station2WorkWithOneTrail() {
        assertEquals(trailOfSizeOne.station2(),route1.station2());
    }

    @Test
    void station2WorkWithTrail() {
        assertEquals(route3.station1(), trailForTestPublic.station2());
    }


    @Test
    void toStringWorks() {
        assertEquals("Genève - Lausanne (14)", longerTrail.toString());
    }



}

