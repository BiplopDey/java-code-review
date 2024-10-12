package schwarz.jobs.interview.coupon.core.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.exceptions.InvalidDiscountException;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.core.services.utils.DecimalsUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    public Optional<Basket> apply(final Basket basket, final String code) {
        return getCoupon(code).map(coupon -> applyCouponToBasket(basket, coupon));
    }

    @Transactional
    public Optional<Coupon> createCoupon(final Coupon coupon) {
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            log.warn("A coupon with the code '{}' already exists.", coupon.getCode());
            return Optional.empty();
        }

        return Optional.of(couponRepository.save(coupon));
    }

    public Set<Coupon> findCouponsByCodes(final List<String> codes) {
        return couponRepository.findByCodeIn(codes);
    }

    private Basket applyCouponToBasket(final Basket basket, final Coupon coupon) {
        validateCouponDiscount(coupon);

        if (isBasketEligibleForDiscount(basket, coupon)) {
            basket.applyDiscount(coupon.getDiscount());
            log.info("Applied coupon");
        } else {
            basket.setApplicationSuccessful(false);
            log.info("Failed to apply coupon");
        }

        return basket;
    }

    private void validateCouponDiscount(final Coupon coupon) {
        if (DecimalsUtil.isNegative(coupon.getDiscount())) {
            throw new InvalidDiscountException("Discount cannot be negative");
        }
    }

    private boolean isBasketEligibleForDiscount(final Basket basket, final Coupon coupon) {
        return DecimalsUtil.isGreaterOrEqual(basket.getValue(), coupon.getMinBasketValue());
    }

    private Optional<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code.toLowerCase());
    }

}
