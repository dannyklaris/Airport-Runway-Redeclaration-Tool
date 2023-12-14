package com.group20seq.runway_redeclaration.Configs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.Arrays;

class AirportTest {

    @Test
    void testAirport() {
        try {
            var airport = Airport.fromXML(new File(Airport.class.getResource("/Airports/Heathrow.xml").toURI()));
            var L09 = airport.getRunwayGroups().get(0).getLower();
            var R27 = airport.getRunwayGroups().get(0).getUpper();
            var R09 = airport.getRunwayGroups().get(1).getLower();
            var L27 = airport.getRunwayGroups().get(1).getUpper();
            var aircraft = new Aircraft(300);

            var obstacle1 = Obstacle.fromXML(new File(Obstacle.class.getResource("/Obstacles/Boeing737.xml").toURI()));
            var obstacle2 = Obstacle.fromXML(new File(Obstacle.class.getResource("/Obstacles/AirbusA380.xml").toURI()));
            var obstacle3 = Obstacle.fromXML(new File(Obstacle.class.getResource("/Obstacles/AirbusA330.xml").toURI()));
            var obstacle4 = Obstacle.fromXML(new File(Obstacle.class.getResource("/Obstacles/Boeing747.xml").toURI()));

            L09.addObstacle(obstacle1, -50 ); L09.TOALO(aircraft);
            R27.addObstacle(obstacle1, 3646); R27.TOTLT(aircraft);
            assertArrayEquals(L09.getRunwayParameters(), new int[]{3346, 3346, 3346, 2985});
            assertArrayEquals(R27.getRunwayParameters(), new int[]{2986, 2986, 2986, 3346});

            R09.addObstacle(obstacle2, 2853); R09.TOTLT(aircraft);
            L27.addObstacle(obstacle2, 500); L27.TOALO(aircraft);
            assertArrayEquals(R09.getRunwayParameters(), new int[]{1850, 1850, 1850, 2553});
            assertArrayEquals(L27.getRunwayParameters(), new int[]{2860, 2860, 2860, 1850});

            R09.addObstacle(obstacle3, 150); R09.TOALO(aircraft);
            L27.addObstacle(obstacle3, 3203); L27.TOTLT(aircraft);
            assertArrayEquals(R09.getRunwayParameters(), new int[]{2903, 2903, 2903, 2393});
            assertArrayEquals(L27.getRunwayParameters(), new int[]{2393, 2393, 2393, 2903});

            L09.addObstacle(obstacle4, 3546); L09.TOTLT(aircraft);
            R27.addObstacle(obstacle4, 50); R27.TOALO(aircraft);
            assertArrayEquals(L09.getRunwayParameters(), new int[]{2792, 2792, 2792, 3246});
            assertArrayEquals(R27.getRunwayParameters(), new int[]{3534, 3612, 3534, 2774});

        } catch (Exception e) {
            e.printStackTrace();
            fail("Error in tests");
        }
    }
}