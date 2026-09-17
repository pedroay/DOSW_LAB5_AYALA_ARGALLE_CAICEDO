package edu.eci.dosw.tdd.skyrescue.center;

import edu.eci.dosw.tdd.skyrescue.drone.Drone;
import edu.eci.dosw.tdd.skyrescue.mission.Mission;
import edu.eci.dosw.tdd.skyrescue.mission.MissionStatus;
import edu.eci.dosw.tdd.skyrescue.operator.RescueOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class RescueCenterTest {

    private RescueCenter rescueCenter;

    @BeforeEach
    public void setup() {
        rescueCenter = new RescueCenter();
    }

    // PRUEBAS PARA EL METODO addDrone
    @Test
    void shouldRegisterDroneWhenDataIsValid() {
        Drone drone = new Drone("DR001", "Model1", 100);
        boolean prueba = rescueCenter.addDrone(drone);
        assertTrue(prueba);
    }

    @Test
    void shouldNotRegisterDroneWhenDroneIsNull() {
        Drone drone = null;
        boolean prueba = rescueCenter.addDrone(drone);
        assertFalse(prueba);
    }

    @Test
    void shouldNotRegisterDroneWhenIdIsEmpty() {
        Drone drone = new Drone("", "Model1", 100);
        boolean prueba = rescueCenter.addDrone(drone);
        assertFalse(prueba);
    }

    // PRUEBAS PARA EL METODO assignMission

    @Test
    void shouldAssignMissionWhenAllDataIsCorrect() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone);
        Mission mission = rescueCenter.assignMission("OP1", "DR001", "LOCATION", 10);
        assertEquals("LOCATION", mission.getLocation());
        assertEquals(operator, mission.getOperator());
        assertEquals(drone, mission.getDrone());
        assertEquals(10, mission.getDistanceKm());
        assertEquals(LocalDateTime.now(), mission.getStartDate());
        assertEquals(MissionStatus.ACTIVE, mission.getStatus());

    }

    @Test
    void shouldNotAssignANonExistentDron() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        rescueCenter.addOperator(operator);
        assertThrows(IllegalArgumentException.class, () -> rescueCenter.assignMission("OP1", "DR001", "LOCATION", 10));
    }

    @Test
    void shouldNotAssignWhenDroneIsAlreadyOccupied() {
        RescueOperator operator1 = new RescueOperator("OP1", "Operator 1");
        RescueOperator operator2 = new RescueOperator("OP2", "Operator 2");
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addOperator(operator1);
        rescueCenter.addOperator(operator2);
        rescueCenter.addDrone(drone);
        rescueCenter.assignMission("OP1", "DR001", "LOCATION_1", 10);
        assertThrows(IllegalStateException.class, () -> rescueCenter.assignMission("OP2", "DR001", "LOCATION_2", 20));
    }

    @Test
    void shouldNotAssignWhenDistanceExceedsDroneAutonomy() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone);
        assertThrows(IllegalArgumentException.class, () -> rescueCenter.assignMission("OP1", "DR001", "LOCATION", 150));
    }

    // PRUEBAS PARA completeMission
    @Test
    void shouldCloseAActiveMission() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone);
        Mission mission = rescueCenter.assignMission("OP1", "DR001", "LOCATION", 10);
        mission = rescueCenter.completeMission(mission.getId());
        assertEquals(MissionStatus.COMPLETED, mission.getStatus());
        assertEquals(operator, mission.getOperator());
        assertEquals(drone, mission.getDrone());
        assertEquals(10, mission.getDistanceKm());
        assertEquals("LOCATION", mission.getLocation());
    }

    @Test
    void shouldThrowAExcpetionBeacuseNonExistentMission() {
        assertThrows(IllegalArgumentException.class, () -> rescueCenter.completeMission(""));
    }

    @Test
    void shouldNotCompleteMissionTwice() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone);
        Mission mission = rescueCenter.assignMission("OP1", "DR001", "LOCATION", 10);
        rescueCenter.completeMission(mission.getId());
        assertThrows(IllegalStateException.class, () -> rescueCenter.completeMission(mission.getId()));
    }

}
