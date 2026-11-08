package com.interview.spring.q128_invoice_payment_service.solution;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

// @RestControllerAdvice = @ControllerAdvice + @ResponseBody:
// catches exceptions from any @RestController in the application context and returns JSON.
// Without this, Spring returns its default error response (Whitelabel Error Page for HTML,
// a sparse JSON object for REST) — no field-level detail, no custom status codes.
//
// Interview question: "What's the difference between @ControllerAdvice and @RestControllerAdvice?"
// Answer: @RestControllerAdvice adds @ResponseBody, so handler return values are serialised
// to JSON automatically. @ControllerAdvice returns views unless @ResponseBody is added per method.
//
// HTTP status mapping rationale:
//   404: resource does not exist — client should not retry as-is
//   409: resource exists but conflicts with current state — client must change resource state first
//   400: bad input — client must fix the request
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(InvoiceNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(InvalidStateTransitionException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadArgument(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }

    // @Valid violations land here. Returns a list of "field: message" strings so the
    // client knows exactly which fields failed and why — better than a generic "bad request."
    // NOT TO DO: catching Exception at the top level and returning 500 for everything —
    // that hides the real error from the client and makes debugging impossible.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleValidation(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .toList();
        return Map.of("errors", errors);
    }
}
