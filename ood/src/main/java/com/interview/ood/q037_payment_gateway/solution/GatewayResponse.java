package com.interview.ood.q037_payment_gateway.solution;

public record GatewayResponse(String gatewayTransactionId, boolean success, String message) {
}
