package edu.eci.dosw.tdd.skyrescue.drone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DroneTest {

    @Test
    void shouldCorrectlyInitializeDroneAndGetters() {
        Drone drone = new Drone("DR001", "DJI Matrice", 50);

        assertEquals("DR001", drone.getId());
        assertEquals("DJI Matrice", drone.getModel());
        assertEquals(50, drone.getMaxRangeKm());
        assertTrue(drone.isAvailable());
    }

    @Test
    void shouldModifyAvailabilityStatus() {
        Drone drone = new Drone("DR001", "DJI Matrice", 50);

        drone.setAvailable(false);
        assertFalse(drone.isAvailable());

        drone.setAvailable(true);
        assertTrue(drone.isAvailable());
    }

    @Test
    void shouldBeEqualAndHaveSameHashCodeWhenSameId() {
        Drone drone1 = new Drone("DR001", "DJI Matrice", 50);
        Drone drone2 = new Drone("DR001", "DJI Phantom", 80);

        assertEquals(drone1, drone2);
        assertEquals(drone1.hashCode(), drone2.hashCode());
        assertEquals(drone1, drone1);
    }

    @Test
    void shouldNotBeEqualWhenDifferentIdOrNullOrDifferentType() {
        Drone drone1 = new Drone("DR001", "DJI Matrice", 50);
        Drone drone2 = new Drone("DR002", "DJI Matrice", 50);

        assertNotEquals(drone1, drone2);
        assertNotEquals(null, drone1);
        assertNotEquals("DR001", drone1);
    }
}