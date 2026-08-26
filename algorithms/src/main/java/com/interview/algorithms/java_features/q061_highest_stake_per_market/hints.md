# Hints — q061 highest_stake_per_market

## Hint 1

Start from `Collectors.toMap`. It has two overloads:

```java
toMap(keyMapper, valueMapper)
toMap(keyMapper, valueMapper, mergeFunction)
```

The 2-arg one throws `IllegalStateException` the instant it sees a second value for a key it's
already collected. You have markets with more than one bet on purpose — that's the 3-arg one's
job. `mergeFunction` is a `BinaryOperator<Bet>` (i.e. `(Bet, Bet) -> Bet`): given the bet
already in the map and the new colliding one, return whichever should win.

Shape:

```java
bets.stream().collect(Collectors.toMap(
        Bet::marketId,
        Function.identity(),
        /* mergeFunction here */
));
```

## Hint 2 (if still stuck)

Don't hand-write the merge function's body as an `if`/`else`. `BinaryOperator` has a static
factory built for exactly "pick the bigger of two, by some comparator":

```java
BinaryOperator.maxBy(Comparator.comparing(Bet::stake))
```

That's a `BinaryOperator<Bet>` on its own — pass it straight in as the third argument.

## Hint 3 (near-full structure)

Full method shape, one blank left:

```java
public static Map<String, Bet> highestStakePerMarket(List<Bet> bets) {
    return bets.stream()
            .collect(Collectors.toMap(
                    Bet::marketId,                                         // key
                    Function.identity(),                                   // value
                    ____________________________________________          // merge on collision
            ));
}
```

Fill the blank with the `BinaryOperator.maxBy(...)` expression from Hint 2. Imports needed:
`java.util.stream.Collectors`, `java.util.function.Function`, `java.util.function.BinaryOperator`,
`java.util.Comparator`.
