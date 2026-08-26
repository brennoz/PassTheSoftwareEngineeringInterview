package com.interview.algorithms.java_features.q054_suppressed_exceptions.solution;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    // Resources close in REVERSE declaration order (B, then A) — the same reason a
    // BufferedInputStream wrapping a FileInputStream must close before the underlying
    // FileInputStream: whatever was opened/wrapped last is the outermost dependency and has
    // to be torn down first, mirroring construction order.
    //
    // The body throws "body failed" — that becomes the PRIMARY exception the try-with-resources
    // statement propagates. Closing B and A then ALSO throw. Their exceptions do NOT replace
    // the primary one and do NOT get lost — the JVM calls primary.addSuppressed(closeException)
    // for each one, in the order they occur (B's close failure first, since B closes first;
    // then A's). getSuppressed() exposes that list. This is exactly why try-with-resources
    // exists over manual try/finally: hand-written finally blocks calling close() themselves
    // would need to replicate this suppressed-exception bookkeeping (or, more commonly, just
    // silently lose the close() exception by letting it overwrite the body's exception —
    // the classic pre-Java-7 bug this feature was introduced to fix).
    public static List<String> describeFailure(boolean resourceAThrowsOnClose, boolean resourceBThrowsOnClose) {
        List<String> result = new ArrayList<>();
        try (LoggingResource a = new LoggingResource("A", resourceAThrowsOnClose);
             LoggingResource b = new LoggingResource("B", resourceBThrowsOnClose)) {
            throw new RuntimeException("body failed");
        } catch (RuntimeException e) {
            result.add("primary: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                result.add("suppressed: " + suppressed.getMessage());
            }
        }
        return result;
    }
}
