package com.interview.algorithms.java_features.q054_suppressed_exceptions.solution;

public class LoggingResource implements AutoCloseable {

    private final String name;
    private final boolean throwOnClose;

    public LoggingResource(String name, boolean throwOnClose) {
        this.name = name;
        this.throwOnClose = throwOnClose;
    }

    @Override
    public void close() {
        if (throwOnClose) {
            throw new RuntimeException("close failed: " + name);
        }
    }
}
