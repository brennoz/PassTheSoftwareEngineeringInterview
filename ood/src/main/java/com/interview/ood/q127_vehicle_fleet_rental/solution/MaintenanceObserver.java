package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.math.BigDecimal;

// Observer interface for vehicle maintenance threshold crossings.
//
// Why Observer and not a boolean return value from returnVehicle()?
// Returning boolean maintenanceRequired from returnVehicle() moves dispatch
// responsibility to every caller: each controller, scheduler, and service must
// check the flag and decide what to do. When a second listener is added
// (compliance logger, customer notification service), every caller must be updated.
//
// Observer keeps "what happened" (mileage crossed) inside the domain.
// "What to do about it" (book a technician, send an alert) belongs to the listeners.
// Vehicle stays stable as the set of reactions grows.
//
// Real-world: in a microservice architecture, this interface would typically be replaced
// by a domain event published to Kafka. The MaintenanceObserver in-process pattern is
// the monolith equivalent — same design reasoning, different delivery mechanism.
public interface MaintenanceObserver {
    void onMaintenanceRequired(String vehicleId, BigDecimal currentMileageKm);
}
