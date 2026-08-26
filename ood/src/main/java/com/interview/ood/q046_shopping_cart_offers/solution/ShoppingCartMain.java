package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;

// Runnable demo — not a test. Walks a Tesco-basket-style checkout: a plain basket total,
// then BOGOF on baked beans layered on top, then a 10% voucher layered on top of THAT — and
// finally the same two offers built in the opposite wrapping order, to make visible that
// order changes the final total.
public class ShoppingCartMain {

    public static void main(String[] args) {
        BasicCart cart = new BasicCart();
        cart.addItem(new CartItem("Baked Beans", new BigDecimal("1.00"), 5));
        cart.addItem(new CartItem("Bread", new BigDecimal("1.20"), 2));

        System.out.println("=== Plain basket, no offers ===");
        System.out.println("Total: " + cart.getTotal());

        System.out.println();
        System.out.println("=== BOGOF on Baked Beans (5 units -> 2 free) ===");
        PricedCart withBogof = new BuyOneGetOneFreeOffer(cart, "Baked Beans");
        System.out.println("Total: " + withBogof.getTotal());

        System.out.println();
        System.out.println("=== BOGOF, then 10% off the discounted total ===");
        PricedCart bogofThenPercent = new PercentageOffOffer(withBogof, new BigDecimal("10"));
        System.out.println("Total: " + bogofThenPercent.getTotal());

        System.out.println();
        System.out.println("=== Same two offers, opposite wrapping order: 10% off first, then BOGOF ===");
        PricedCart withPercent = new PercentageOffOffer(cart, new BigDecimal("10"));
        PricedCart percentThenBogof = new BuyOneGetOneFreeOffer(withPercent, "Baked Beans");
        System.out.println("Total: " + percentThenBogof.getTotal());
        System.out.println("(Different from the previous total — wrapping order changed the result.)");
    }
}
