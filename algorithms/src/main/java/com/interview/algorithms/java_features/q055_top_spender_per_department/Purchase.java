package com.interview.algorithms.java_features.q055_top_spender_per_department;

import java.math.BigDecimal;

public record Purchase(String employeeName, String department, BigDecimal amount) {}
