package com.interview.algorithms.java_features.q060_top_clean_transactions.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Trade-off vs Solution.java: the imperative version needs an explicit intermediate ArrayList
// for the "clean" bucket (partitioningBy gives you both buckets in one pass, keyed by
// boolean); and Collections.sort mutates that list in place, whereas Stream.sorted() returns a
// new ordered stream and leaves the source untouched. Functionally identical result, but the
// stream version is harder to get wrong by accident — there's no separate "did I sort before
// or after building the sublist" step to get backwards.
public class ImperativeApproach {

    public static List<Transaction> topCleanTransactions(List<Transaction> transactions, int topN) {
        List<Transaction> clean = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (!transaction.flaggedForReview()) {
                clean.add(transaction);
            }
        }

        Collections.sort(clean, Comparator.comparing(Transaction::amount).reversed()
                .thenComparing(Transaction::id));

        List<Transaction> top = new ArrayList<>();
        for (int i = 0; i < clean.size() && i < topN; i++) {
            top.add(clean.get(i));
        }
        return top;
    }
}
