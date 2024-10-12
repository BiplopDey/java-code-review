package schwarz.jobs.interview.coupon.core.services.model;

import lombok.Builder;
import lombok.Data;
import schwarz.jobs.interview.coupon.core.services.exceptions.InvalidDiscountException;
import schwarz.jobs.interview.coupon.core.services.utils.DecimalsUtil;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
public class Basket {

    @NotNull
    private BigDecimal value;

    private BigDecimal appliedDiscount;

    private boolean applicationSuccessful;

    public void applyDiscount(final BigDecimal discount) {
        validate(discount);

        this.appliedDiscount = discount;
        this.value = this.value.subtract(discount);
        this.applicationSuccessful = true;
    }

    private void validate(BigDecimal discount) {
        if (Objects.isNull(this.value)) {
            throw new IllegalArgumentException("Basket value cannot be null");
        }
        if (Objects.isNull(discount)) {
            throw new InvalidDiscountException("Discount cannot be null");
        }
        if (DecimalsUtil.isNegative(discount)) {
            throw new InvalidDiscountException("Discount cannot be negative");
        }
        if (isGreaterThanBasketValue(discount)) {
            throw new InvalidDiscountException("Discount cannot exceed basket value");
        }
    }

    private boolean isGreaterThanBasketValue(BigDecimal discount) {
        return discount.compareTo(this.value) > 0;
    }
}
