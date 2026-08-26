package com.interview.algorithms.java_features.q054_suppressed_exceptions.solution;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void neitherResourceThrowsOnCloseYieldsOnlyPrimary() {
        List<String> result = Solution.describeFailure(false, false);

        assertEquals(List.of("primary: body failed"), result);
    }

    @Test
    void onlySecondDeclaredResourceThrowsOnClose() {
        List<String> result = Solution.describeFailure(false, true);

        assertEquals(List.of("primary: body failed", "suppressed: close failed: B"), result);
    }

    // What this verifies: close order is LIFO (reverse declaration order), and suppressed
    // exceptions are attached in that same order — B (declared second, closes first) appears
    // before A in the suppressed list. A version that assumed declaration order == close
    // order would produce A before B here, which is backwards.
    @Test
    void bothResourcesThrowOnCloseInReverseDeclarationOrder() {
        List<String> result = Solution.describeFailure(true, true);

        assertEquals(List.of(
                "primary: body failed",
                "suppressed: close failed: B",
                "suppressed: close failed: A"
        ), result);
    }
}
