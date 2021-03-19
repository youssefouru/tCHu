package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StationPartitionTest {


    @Test
    void connectsWorksWell(){
        StationPartition.Builder builder = new StationPartition.Builder(14);
        Station station0 = ChMap.stations().get(0);
        Station station1 = ChMap.stations().get(1);
        Station station2 = ChMap.stations().get(2);
        Station station3 = ChMap.stations().get(3);
        Station station4 = ChMap.stations().get(4);
        Station station5 = ChMap.stations().get(5);
        Station station6 = ChMap.stations().get(6);
        Station station7 = ChMap.stations().get(7);
        Station station8 = ChMap.stations().get(8);
        Station station9 = ChMap.stations().get(9);
        Station station10 = ChMap.stations().get(10);
        Station station11 = ChMap.stations().get(11);
        Station station12 = ChMap.stations().get(12);
        Station station13 = ChMap.stations().get(13);
        builder.connect(station1,station0);
        builder.connect(station11,station2);
        builder.connect(station1,station2);


        builder.connect(station3,station5);
        builder.connect(station6,station5);


        builder.connect(station10,station9);
        builder.connect(station4,station7);
        builder.connect(station4,station10);


        builder.connect(station8,station12);
        builder.connect(station13,station8);

        StationPartition stationPartition = builder.build();

        assertTrue(stationPartition.connected(station1,station0));
        assertTrue(stationPartition.connected(station1,station11));

        assertTrue(stationPartition.connected(station3,station6));
        assertTrue(stationPartition.connected(station3,station5));

        assertFalse(stationPartition.connected(station1,station10));

        assertFalse(stationPartition.connected(ChMap.stations().get(18),ChMap.stations().get(13)));
        assertFalse(stationPartition.connected(ChMap.stations().get(13),ChMap.stations().get(18)));
        assertFalse(stationPartition.connected(ChMap.stations().get(18),ChMap.stations().get(34)));
        assertTrue(stationPartition.connected(ChMap.stations().get(18),ChMap.stations().get(18)));



    }

    @Test
    void constructorFailsWithNegatifCount(){
        assertThrows(IllegalArgumentException.class, ()->{
           new StationPartition.Builder(-5);
        });
    }

}
