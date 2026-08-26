package com.interview.algorithms.java_features.q060_top_clean_transactions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyAnswer {

    public static List<Transaction> topCleanTransactions(List<Transaction> transactions, int topN) {

        Map<Boolean, List<Transaction>> groupedByFlag = transactions.stream()
                    .collect(Collectors.partitioningBy(tx -> tx.flaggedForReview()));

        List<Transaction> flaggedTxs = groupedByFlag.get(Boolean.TRUE);

        if (!flaggedTxs.isEmpty() && flaggedTxs.size() < topN) {
            return flaggedTxs;
        }

        return flaggedTxs.stream()
                         .sorted(Comparator.comparing(Transaction::amount).reversed().thenComparing(Transaction::id))
                         .limit(topN)
                         .toList();
    }
}
