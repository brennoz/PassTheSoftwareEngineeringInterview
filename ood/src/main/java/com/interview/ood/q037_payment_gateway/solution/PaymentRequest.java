package com.interview.ood.q037_payment_gateway.solution;

import java.util.Objects;

// Pattern: Builder — PaymentRequest has 6 fields, most required, one (require3ds) optional
// with a sane default (false). A telescoping constructor here would force callers to pass
// `null, false` for fields they don't care about, or overload the constructor per
// combination. Builder makes construction self-documenting at the call site
// (.cardToken(...).amount(...)) instead of positional args that are easy to swap by mistake.
public record PaymentRequest(
        String cardToken,
        Money amount,
        String merchantId,
        String merchantReference,
        String description,
        boolean require3ds
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String cardToken;
        private Money amount;
        private String merchantId;
        private String merchantReference;
        private String description;
        private boolean require3ds = false;

        public Builder cardToken(String cardToken) {
            this.cardToken = cardToken;
            return this;
        }

        public Builder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public Builder merchantId(String merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder merchantReference(String merchantReference) {
            this.merchantReference = merchantReference;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder require3ds() {
            this.require3ds = true;
            return this;
        }

        public PaymentRequest build() {
            Objects.requireNonNull(cardToken, "cardToken required");
            Objects.requireNonNull(amount, "amount required");
            Objects.requireNonNull(merchantId, "merchantId required");
            Objects.requireNonNull(merchantReference, "merchantReference required");
            return new PaymentRequest(cardToken, amount, merchantId, merchantReference, description, require3ds);
        }
    }
}
