package com.interview.pairingdrill.q073_order_fulfillment.solution;

public interface ShippingNotifier {

    void sendDispatchNotification(Order order) throws NotificationException;

    void sendRedispatchNotification(Order order) throws NotificationException;
}
