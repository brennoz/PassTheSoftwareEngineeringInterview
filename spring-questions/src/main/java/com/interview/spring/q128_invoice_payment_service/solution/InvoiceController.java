package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController = @Controller + @ResponseBody: every method return value is serialised
// to JSON. No @ResponseBody needed per method.
//
// Controller responsibility: HTTP translation ONLY.
//   - Extract path variables, request params, request body
//   - Call the service
//   - Map the result to a response DTO
//   - Return with the correct HTTP status
//
// NOT TO DO: business logic in the controller.
// "Invoice is ISSUED so payment is valid" — that's domain logic → Invoice.applyPayment().
// "Load invoice, modify, save" — that's orchestration → InvoiceService.
// Controllers that contain if/else state checks are a maintenance nightmare: the same logic
// ends up duplicated in tests, other controllers, and CLI entry points.
//
// @RequestMapping("/api/invoices"): base path for all endpoints in this controller.
// All downstream @GetMapping / @PostMapping paths are relative to this.
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService service;

    // Constructor injection — see InvoiceService for the rationale.
    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    // 201 Created: a new resource was created and can be accessed at /api/invoices/{id}.
    // @ResponseStatus(CREATED) sets the HTTP status — without it, Spring defaults to 200.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponse createInvoice(@RequestBody @Valid CreateInvoiceRequest request) {
        return InvoiceResponse.from(
            service.createInvoice(request.customerId(), request.dueDate()));
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoice(@PathVariable String id) {
        return InvoiceResponse.from(service.getInvoice(id));
    }

    // @RequestParam: /api/invoices?customerId=CUST-001
    // Returns empty list (not 404) when no invoices exist for the customer.
    @GetMapping
    public List<InvoiceResponse> getInvoicesForCustomer(@RequestParam String customerId) {
        return service.getInvoicesForCustomer(customerId).stream()
            .map(InvoiceResponse::from)
            .toList();
    }

    @PostMapping("/{id}/line-items")
    public InvoiceResponse addLineItem(@PathVariable String id,
                                       @RequestBody @Valid AddLineItemRequest request) {
        return InvoiceResponse.from(
            service.addLineItem(id, request.description(), request.unitPrice(), request.quantity()));
    }

    // State-change actions are POSTs to sub-resources, not PUT/PATCH to the invoice.
    // Why? PUT/PATCH implies the client sends the new state — but the client should not
    // be able to set status = "PAID" arbitrarily. Sub-resource actions make the intent
    // explicit and prevent clients from bypassing the state machine.
    @PostMapping("/{id}/issue")
    public InvoiceResponse issueInvoice(@PathVariable String id) {
        return InvoiceResponse.from(service.issueInvoice(id));
    }

    @PostMapping("/{id}/payments")
    public InvoiceResponse recordPayment(@PathVariable String id,
                                          @RequestBody @Valid RecordPaymentRequest request) {
        return InvoiceResponse.from(service.recordPayment(id, request.amount()));
    }

    @PostMapping("/{id}/cancel")
    public InvoiceResponse cancelInvoice(@PathVariable String id) {
        return InvoiceResponse.from(service.cancelInvoice(id));
    }

    // 204 No Content: success, nothing to return.
    // Only DRAFT invoices can be hard-deleted (enforced in service).
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoice(@PathVariable String id) {
        service.deleteInvoice(id);
    }
}
