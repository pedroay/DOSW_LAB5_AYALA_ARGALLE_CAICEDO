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

    @Test
    void shouldNotRegisterDuplicateDroneID() {
        Drone drone1 = new Drone("DR001", "Model1", 100);
        Drone drone2 = new Drone("DR001", "Model2", 150);

        boolean primerRegistro = rescueCenter.addDrone(drone1);
        boolean segundoRegistro = rescueCenter.addDrone(drone2);
        assertFalse(segundoRegistro);
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
        assertNotNull(mission.getStartDate());
        assertFalse(mission.getStartDate().isAfter(LocalDateTime.now()));
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

    @Test
    void shouldThrowExceptionWhenOperatorDoesNotExist() {
        Drone drone = new Drone("DR001", "Model1", 100);
        rescueCenter.addDrone(drone);
        assertThrows(IllegalArgumentException.class, () -> rescueCenter.assignMission("OP_INEXISTENTE", "DR001", "LOCATION", 10));
    }

    @Test
    void shouldThrowExceptionWhenOperatorAlreadyHasActiveMission() {
        RescueOperator operator = new RescueOperator("OP1", "NAME");
        Drone drone1 = new Drone("DR001", "Model1", 100);
        Drone drone2 = new Drone("DR002", "Model2", 100);

        rescueCenter.addOperator(operator);
        rescueCenter.addDrone(drone1);
        rescueCenter.addDrone(drone2);
        // Asignación m1
        rescueCenter.assignMission("OP1", "DR001", "LOCATION 1", 10);
        // Intento asignar m2
        assertThrows(IllegalStateException.class, () -> rescueCenter.assignMission("OP1", "DR002", "LOCATION 2", 20));
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

    @Test
void shouldNotModifyOtherActiveMissionWhenOneIsCompleted() {
    RescueOperator op1 = new RescueOperator("OP1", "Operador 1");
    RescueOperator op2 = new RescueOperator("OP2", "Operador 2");
    Drone drone1 = new Drone("DR001", "Model1", 100);
    Drone drone2 = new Drone("DR002", "Model2", 100);
    rescueCenter.addOperator(op1);
    rescueCenter.addOperator(op2);
    rescueCenter.addDrone(drone1);
    rescueCenter.addDrone(drone2);
    Mission mission1 = rescueCenter.assignMission("OP1", "DR001", "Zona 1", 10);
    Mission mission2 = rescueCenter.assignMission("OP2", "DR002", "Zona 2", 20);
    // Completar m1
    rescueCenter.completeMission(mission1.getId());
    // Verificar que m2 y el dron no cambien
    assertEquals(MissionStatus.ACTIVE, mission2.getStatus());
    assertNull(mission2.getEndDate());
    assertFalse(drone2.isAvailable(), "El dron de la otra misión debe seguir estando ocupado");
}

}
