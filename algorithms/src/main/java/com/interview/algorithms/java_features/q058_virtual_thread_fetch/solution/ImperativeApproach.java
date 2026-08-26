package com.interview.algorithms.java_features.q058_virtual_thread_fetch.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// The genuinely "no concurrency at all" baseline — a plain sequential loop. Kept separate so
// Main.java can time both against the same input and show the actual gap, not just assert it
// exists.
//
// Order preservation is trivial here — there's nothing to preserve, since nothing ever runs
// out of order to begin with. That's the real trade-off point: a sequential loop can't get the
// order wrong because it has no concurrency to get wrong, but that safety comes at the cost of
// total wall-clock time being the SUM of every call's duration rather than roughly the
// duration of the SLOWEST single call, which is what fetchAllConcurrently achieves. For
// I/O-bound work (network calls, this exercise's simulated "slow fetch"), that difference is
// often the entire point of introducing concurrency in the first place.
public class ImperativeApproach {

    public static List<String> fetchAllConcurrently(List<String> customerIds, Function<String, String> fetchCustomerData) {
        List<String> results = new ArrayList<>();
        for (String id : customerIds) {
            results.add(fetchCustomerData.apply(id));
        }
        return results;
    }
}
