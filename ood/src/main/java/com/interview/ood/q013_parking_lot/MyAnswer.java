package com.interview.ood.q013_parking_lot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Design decisions (fill this in before implementing):
 *
 * 1. Finding available spot: ...
 * 2. Thread safety: ...
 * 3. SOLID principles applied: ...
 */

// TODO: implement all classes here (or as separate files in this package)
// Reference structure in Solution.java
public class MyAnswer {
    public static void main (String[] args) {
        //Vehicle car = VehicleFactory.getInstance(VehicleType.CAR);
        
    }
}

enum SpotType {
    MOTORCYCLE, COMPACT, LARGE
}

enum VehicleType {
    MOTORCYCLE, CAR, TRUCK
}

class VehicleFactory {

    public static Vehicle getInstance (VehicleType type, String licensePlate) {
        Vehicle vehicle;
        switch (type) {
            case MOTORCYCLE  : vehicle = new Motorcycle(licensePlate);
                          break;
            case CAR         : vehicle = new Car(licensePlate);
                          break;
            case TRUCK       : vehicle = new Truck(licensePlate);
                          break;
            default :     throw new RuntimeException("This type is not valid!");                                                    
        }
        return vehicle;
    }
}

abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;
    public Vehicle (String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    abstract boolean fitsIn(SpotType spotType);
}

class Motorcycle extends Vehicle {  
    public Motorcycle (String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }

    public boolean fitsIn(SpotType spotType) {

        if (spotType == SpotType.MOTORCYCLE
            || spotType == SpotType.COMPACT
            || spotType == SpotType.LARGE
        ) return true;

        return false;
    }
}

class Car extends Vehicle {
    public Car (String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }

    public boolean fitsIn(SpotType spotType) {
        
        if (spotType == SpotType.COMPACT
            || spotType == SpotType.LARGE
        ) return true;

        return false;
    }    
}

class Truck extends Vehicle {
    public Truck (String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }

    public boolean fitsIn(SpotType spotType) {
        
        if (spotType == SpotType.LARGE
        ) return true;

        return false;
    }    
}

class ParkingLot {

    //Parking lot has multiple floors, each floor has multiple spots
    private List<List<ParkingSpot>> floors;

    public ParkingLot() {
        this.floors = new ArrayList<>();
    }

    public ParkingLot(int nOfFloors) {
        this.floors = new ArrayList<>(nOfFloors);
        for (int i = 0; i < nOfFloors; i++) floors.add(new ArrayList<>());
    }

    public void add(ParkingSpot spot) {
        if (spot.getFloor() >= floors.size()) throw new IllegalArgumentException("Floor does not exist!");
        List<ParkingSpot> floor = floors.get(spot.getFloor());
        floor.add(spot);
    }

    public synchronized Ticket park (Vehicle vehicle) {
        //search for 1st available slot based on vehicle type using isFullFor(VehicleType)?
        for (int i = 0; i < floors.size(); i++) {
            List<ParkingSpot> floor = floors.get(i);
            for (int j = 0; j < floor.size(); j++) {
                ParkingSpot spot = floor.get(j);
                if (vehicle.fitsIn(spot.getSpotType()) && spot.isAvailable()) {
                    spot.setAvailable(false);
                    return new Ticket(vehicle, spot);
                }
            }
        }
        throw new RuntimeException("Spot not available or unfit");
    }

    public synchronized double leave(Ticket ticket) {
        ticket.getSpot().setAvailable(true);
        long hours = java.time.temporal.ChronoUnit.HOURS.between(ticket.getEntryTime(), LocalDateTime.now());
        return Math.max(1, hours) * 2.0;
    }

    public boolean isFullFor(VehicleType type) {
        Vehicle probe = VehicleFactory.getInstance(type, "probe");
        for (List<ParkingSpot> floor : floors) {
            for (ParkingSpot spot : floor) {
                if (spot.isAvailable() && probe.fitsIn(spot.getSpotType())) return false;
            }
        }
        return true;
    }
}

class Ticket {

    private static final AtomicInteger ID = new AtomicInteger(0);

    private int ticketId;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private LocalDateTime entryTime;
    
    public Ticket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ID.incrementAndGet();
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = LocalDateTime.now();
    }
    public ParkingSpot getSpot() { return this.spot; }
    public LocalDateTime getEntryTime() { return this.entryTime; }
}

class ParkingSpot {
    private int spotId;
    private int floor;
    private SpotType type;
    private boolean available;

    public ParkingSpot (int spotId, int floor, SpotType type, boolean available) {
        this.spotId = spotId;
        this.floor  = floor;
        this.type   = type;
        this.available = available;
    }

    public int getFloor() {
        return this.floor;
    }

    public SpotType getSpotType() {
        return this.type;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }    
}
