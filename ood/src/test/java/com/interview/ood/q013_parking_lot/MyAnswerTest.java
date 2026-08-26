package com.interview.ood.q013_parking_lot;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class MyAnswerTest {

    // Build a small lot: 1 MOTORCYCLE, 1 COMPACT, 1 LARGE spot
    private ParkingLot buildSmallLot() {
        // TODO: construct your ParkingLot with spots
        ParkingLot parkingLot = new ParkingLot(3);
        // Replace with your constructor once implemented
        ParkingSpot spot = new ParkingSpot(1, 0, SpotType.COMPACT, true);
        parkingLot.add(spot);
        spot = new ParkingSpot(2, 0, SpotType.COMPACT, true);
        parkingLot.add(spot);
        spot = new ParkingSpot(3, 0, SpotType.LARGE, true);
        parkingLot.add(spot);  
        spot = new ParkingSpot(4, 1, SpotType.MOTORCYCLE, true);
        parkingLot.add(spot);  
        spot = new ParkingSpot(5, 1, SpotType.MOTORCYCLE, true);
        parkingLot.add(spot);  
        spot = new ParkingSpot(6, 1, SpotType.COMPACT, true);
        parkingLot.add(spot);                                

        return parkingLot;
    }

    @Test
    void carParksInCompactSpotAndReceivesTicket() {
        ParkingLot lot = buildSmallLot();
        Ticket ticket = lot.park(new Car("ABC-123"));
        assertThat(ticket).isNotNull();
        assertThat(ticket.getSpot().getSpotType()).isIn(SpotType.COMPACT, SpotType.LARGE);
    }

    @Test
    void lotFullForTruckWhenNoLargeSpotAvailable() {
        ParkingLot lot = buildSmallLot();
        lot.park(new Truck("TRK-001"));  // occupies the only LARGE spot
        assertThat(lot.isFullFor(VehicleType.TRUCK)).isTrue();
    }
}
