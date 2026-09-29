package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// The reduce-vs-Collectors comparison, plus a char-level String.reduce example — covers
// both plausible readings of "String reduce" from the interviewer's question list.
public class ReferenceBuilder {

    // Stream.reduce with NO identity: returns Optional<String> because an empty stream has
    // no result to fall back to. reduce folds elements pairwise into a single accumulated
    // value with no intermediate mutable container — semantically a pure fold.
    public Optional<String> buildReferenceUsingReduce(List<String> parts) {
        return parts.stream().reduce((a, b) -> a + "-" + b);
    }

    // Collectors.joining is a MUTABLE reduction under the hood (a StringBuilder is
    // accumulated internally) — it's the idiomatic, more efficient choice specifically for
    // joining strings (no per-step String concatenation/copy the way naive reduce does),
    // and it never needs an Optional: an empty input just joins to "".
    public String buildReferenceUsingCollectors(List<String> parts) {
        return parts.stream().collect(Collectors.joining("-"));
    }

    // Literal "String .reduce()" — IntStream.reduce over a String's char codes. A common use:
    // a simple additive checksum over a sort code / account number. identity=0 means an
    // empty string safely reduces to 0 rather than needing Optional handling.
    public int checksum(String input) {
        return input.chars().reduce(0, Integer::sum);
    }
}
