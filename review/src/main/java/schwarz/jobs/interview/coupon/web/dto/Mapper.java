package schwarz.jobs.interview.coupon.web.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import schwarz.jobs.interview.coupon.core.domain.Coupon;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mapper {

    public static Coupon toCoupon(CouponDTO couponDTO) {
        if (Objects.isNull(couponDTO.getCode())) {
            throw new IllegalArgumentException("Coupon code cannot be null");
        }
        return Coupon.builder()
                .code(couponDTO.getCode().toLowerCase())
                .discount(couponDTO.getDiscount())
                .minBasketValue(couponDTO.getMinBasketValue())
                .build();
    }
}
