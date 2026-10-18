package com.interview.pairingdrill.q078_payment_search;

import java.util.List;

public interface PaymentRepository {
    List<Payment> findAll();
}
