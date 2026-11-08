package com.interview.pairingdrill.q081_invoice_billing_service;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Optional<Invoice> findById(String id);
    Invoice save(Invoice invoice);
    List<Invoice> findByCustomerIdAndStatus(String customerId, String status);
}
