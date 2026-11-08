package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Aggregate root: owns all state and behaviour for a single fleet vehicle.
//
// Three patterns compose here without coupling each other:
//   State machine  — VehicleStatus.canTransitionTo() guards every transitionTo() call
//   Strategy       — pricingStrategy.calculateCost() handles all pricing decisions
//   Observer       — notifyObservers() fires when a mileage threshold is crossed on return
//
// None of the three patterns knows about the others. Vehicle is the only place
// that orchestrates them — it delegates, never computes pricing or evaluates thresholds itself.
//
// NOT TO DO: exposing a setStatus(VehicleStatus) setter.
// That bypasses all transition guards. Any caller could write
// vehicle.setStatus(VehicleStatus.RETIRED) from any state, silently corrupting the lifecycle.
// All state changes go through transitionTo() — the only method that calls canTransitionTo().
public class Vehicle {

    private final String id;
    private final VehicleClass vehicleClass;
    private final PricingStrategy pricingStrategy;

    private VehicleStatus status;
    private BigDecimal currentMileage;
    private BigDecimal mileageAtCheckout;     // odometer reading when keys were handed over
    private final BigDecimal maintenanceThresholdKm;
    private final List<MaintenanceObserver> observers = new ArrayList<>();

    public Vehicle(String id,
                   VehicleClass vehicleClass,
                   BigDecimal initialMileage,
                   BigDecimal maintenanceThresholdKm) {
        this.id                     = id;
        this.vehicleClass           = vehicleClass;
        this.status                 = VehicleStatus.AVAILABLE;
        this.currentMileage         = initialMileage;
        this.mileageAtCheckout      = initialMileage;
        this.maintenanceThresholdKm = maintenanceThresholdKm;
        // Factory resolves the correct strategy from the class — Vehicle doesn't know
        // which rate or tier rules apply; it just holds a reference and delegates.
        this.pricingStrategy        = PricingStrategyFactory.forClass(vehicleClass);
    }

    // --- Observer management ---

    public void addObserver(MaintenanceObserver observer) {
        observers.add(observer);
    }

    // --- State transitions ---

    // customerId is recorded for future audit log extension; not used in this iteration.
    // Adding it to the signature now avoids a breaking interface change later.
    public void reserve(String customerId) {
        transitionTo(VehicleStatus.RESERVED);
    }

    public void cancelReservation() {
        transitionTo(VehicleStatus.AVAILABLE);
    }

    public void checkout(BigDecimal mileageAtStart) {
        transitionTo(VehicleStatus.IN_USE);
        // Store the odometer at the start of this rental to determine whether the
        // maintenance threshold is crossed DURING this rental (not before it).
        // Without this, a vehicle already past the threshold would trigger a
        // duplicate maintenance alert on every subsequent return.
        this.mileageAtCheckout = mileageAtStart;
        this.currentMileage    = mileageAtStart;
    }

    public void returnVehicle(BigDecimal mileageAtReturn) {
        transitionTo(VehicleStatus.AWAITING_INSPECTION);
        this.currentMileage = mileageAtReturn;
        // Notify only when the threshold is crossed during THIS rental.
        // If the vehicle was already past the threshold at checkout, a work order
        // was already raised — a second notification would create a duplicate and
        // potentially double-book a technician or overcount service events.
        boolean wasBeforeThreshold  = mileageAtCheckout.compareTo(maintenanceThresholdKm) < 0;
        boolean isAtOrPastThreshold = mileageAtReturn.compareTo(maintenanceThresholdKm) >= 0;
        if (wasBeforeThreshold && isAtOrPastThreshold) {
            notifyObservers();
        }
    }

    public void inspectionPassed() {
        transitionTo(VehicleStatus.AVAILABLE);
    }

    public void inspectionFailed() {
        transitionTo(VehicleStatus.MAINTENANCE);
    }

    public void repairComplete() {
        transitionTo(VehicleStatus.AVAILABLE);
    }

    public void retire() {
        transitionTo(VehicleStatus.RETIRED);
    }

    // --- Pricing ---

    // Delegates entirely to the pricing strategy. Vehicle has no knowledge of
    // tiers, rates, or multipliers — it asks the strategy and returns the result.
    public Money getRentalCost(RentalPeriod period) {
        return pricingStrategy.calculateCost(period);
    }

    // --- Helpers ---

    private void transitionTo(VehicleStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidStateTransitionException(id, status, next);
        }
        this.status = next;
    }

    private void notifyObservers() {
        for (MaintenanceObserver observer : observers) {
            observer.onMaintenanceRequired(id, currentMileage);
        }
    }

    // --- Accessors ---

    public String getId()                 { return id; }
    public VehicleClass getVehicleClass() { return vehicleClass; }
    public VehicleStatus getStatus()      { return status; }
    public BigDecimal getCurrentMileage() { return currentMileage; }

    // Defensive copy — exposes observers for testing/diagnostics without allowing
    // external modification. Not part of the domain API; callers should not depend on this.
    public List<MaintenanceObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }
}
