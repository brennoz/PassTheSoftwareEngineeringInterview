package com.interview.algorithms.java_features.q057_lazy_optional_callbacks.solution;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Same two methods as Solution.java, solved with plain if/else and Optional.isPresent()/get()
// instead of orElseGet()/ifPresentOrElse() — kept separate so the two approaches can be read
// side by side, not because isPresent()+get() is the recommended style in this project
// (elsewhere it explicitly isn't, see q049).
//
// The key trade-off point this file exists to make: LAZINESS COMES FOR FREE HERE. In
// greet(), the else branch's fallbackGreetingSupplier.get() call is inside the else block —
// it simply doesn't execute unless that branch runs. Nothing special had to be done to
// achieve that; it's just how if/else always works. Contrast with the functional
// Solution.java, where getting the same laziness required specifically choosing
// orElseGet(Supplier) over orElse(T) — the fluent, single-expression style doesn't get
// short-circuiting for free the way branching does, because Java evaluates a method's
// arguments eagerly before the method runs. That's the actual reason orElseGet exists as a
// distinct method from orElse, rather than orElse just being smart enough to only evaluate
// its argument when needed.
//
// Where the imperative version loses, in exchange: notifyCustomer() needs an explicit
// isPresent() check and a manual .get() call, both of which are exactly the pattern q049
// calls out as an anti-pattern to avoid in idiomatic Optional-using code — there's no
// equivalent risk of forgetting a null/absence check in the functional version, since
// ifPresentOrElse() structurally forces you to supply behaviour for both branches.
public class ImperativeApproach {

    public static String greet(Optional<Customer> maybeCustomer, Supplier<String> fallbackGreetingSupplier) {
        if (maybeCustomer.isPresent()) {
            Customer customer = maybeCustomer.get();
            return "Welcome back, " + customer.email();
        } else {
            return fallbackGreetingSupplier.get();
        }
    }

    public static void notifyCustomer(Optional<Customer> maybeCustomer, Consumer<Customer> sendNotification, Runnable logNoCustomerFound) {
        if (maybeCustomer.isPresent()) {
            Customer customer = maybeCustomer.get();
            sendNotification.accept(customer);
        } else {
            logNoCustomerFound.run();
        }
    }
}
