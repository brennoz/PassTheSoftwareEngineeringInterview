**Lessons:** [0001 Shopping Cart Offers Decorator](../../../../../../../../topics/ood/shopping-cart/0001-shopping-cart-offers-decorator.html)

# q046 — Shopping Cart with Offers

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 25 min

---

## Problem

A well-known supermarket-checkout kata (Tesco-basket style): a shopping cart's total price
must be recalculated when promotional offers are applied — "Buy One Get One Free" on a
specific product, a percentage-off voucher on the whole basket — and offers must be
**stackable in any combination the caller chooses**, with the checkout total reflecting
every offer applied, in the order they were applied. Use the **Decorator Pattern** so each
offer wraps the cart and adds its own pricing behaviour without modifying the cart or any
other offer.

---

## Requirements

1. `CartItem` — a value object: `productName` (String), `unitPrice` (`BigDecimal`),
   `quantity` (int).
2. `PricedCart` — the **Decorator Pattern** component interface: `List<CartItem> getItems()`,
   `BigDecimal getTotal()`.
3. `BasicCart` (**Concrete Component**) implements `PricedCart`: holds a
   `List<CartItem>`, `addItem(CartItem)`. `getTotal()` sums `unitPrice * quantity` across all
   items, no offers applied.
4. `CartOfferDecorator` — abstract base **Decorator**, implements `PricedCart`, wraps another
   `PricedCart`. `getItems()` must delegate straight through to the wrapped cart unchanged —
   decorators recompute price, they never alter what's actually in the basket.
5. `BuyOneGetOneFreeOffer` (concrete decorator): targets one product by name. `getTotal()` =
   wrapped total minus `(quantity / 2) * unitPrice` for that product (integer division —
   an odd quantity's leftover unit is not free).
6. `PercentageOffOffer` (concrete decorator): applies a percentage discount (`BigDecimal`,
   e.g. `10` for 10%) to the **wrapped total** — i.e. whatever total the cart/decorators
   underneath it already produced, not the original undiscounted total.
7. Offers must be stackable: wrapping a `BasicCart` in a `BuyOneGetOneFreeOffer`, then
   wrapping that in a `PercentageOffOffer`, must apply BOGOF first and take the percentage
   off the already-discounted total — **order of wrapping must change the result**.
8. `BigDecimal` for every price — never `double`/`float`.

---

## Class diagram (skeleton guide)

```
PricedCart (interface)             ← Decorator component
  BasicCart                        ← Concrete Component
  CartOfferDecorator (abstract)    ← Decorator base, wraps a PricedCart
    BuyOneGetOneFreeOffer          ← Concrete Decorator
    PercentageOffOffer             ← Concrete Decorator

CartItem   (productName, BigDecimal unitPrice, int quantity)
```

---

## Constraints

- `CartOfferDecorator.getItems()` must be a pure pass-through — no decorator ever edits the
  underlying item list, only the computed total.
- Do not hardcode which offers apply in `BasicCart` or in each other — every decorator must
  only know about the `PricedCart` interface it wraps, not concrete `BasicCart` or sibling
  decorator types.
- `BuyOneGetOneFreeOffer` must use integer division on quantity (`quantity / 2`) — a
  quantity of 5 gives 2 free units, not 2.5.
- No Spring, no persistence, pure Java 21.

---

## Folder layout

(Standard for `next ood` — see CLAUDE.md's "OOD Delivery Convention" section: full
solution-walkthrough lesson + complete `solution/` (including a runnable
`ShoppingCartMain.java`) + empty `answer/`, mirrored under both `src/main` and `src/test`.)

```
q046_shopping_cart_offers/
├── question.md
├── solution/   ← full reference implementation, one class per file, TDD-ordered, includes ShoppingCartMain.java
└── answer/     ← empty (.gitkeep) — write from scratch, or study the lesson + solution/ first
```

---

## Hints

If stuck → type `hint`
To see full solution → type `solution`
To submit → type `done`
