package com.interview.pairingdrill.q073_order_fulfillment.solution;

public class Customer {

    private Long id;
    private Address address;

    public Customer() {
    }

    public Customer(Long id, Address address) {
        this.id = id;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
