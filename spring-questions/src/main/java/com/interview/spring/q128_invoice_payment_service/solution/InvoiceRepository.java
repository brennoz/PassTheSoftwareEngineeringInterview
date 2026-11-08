package com.interview.spring.q128_invoice_payment_service.solution;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository<Invoice, String>: Invoice entity, String ID type (UUID string).
// JpaRepository provides: findById, save, delete, findAll, existsById — no implementation needed.
//
// Spring Data JPA generates the implementation at startup from the method name.
// findByCustomerId: SELECT * FROM invoices WHERE customer_id = ?1
// findByStatus: SELECT * FROM invoices WHERE status = ?1
// No SQL written — naming convention is the spec.
//
// Interview question: "How does Spring Data JPA know what SQL to run?"
// Answer: "It parses the method name at startup and generates a JPQL query. For complex queries
// use @Query(\"SELECT i FROM Invoice i WHERE ...\") with JPQL or nativeQuery = true for raw SQL."
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findByCustomerId(String customerId);

    List<Invoice> findByStatus(InvoiceStatus status);
}
