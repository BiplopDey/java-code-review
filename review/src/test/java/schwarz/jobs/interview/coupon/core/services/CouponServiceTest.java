package schwarz.jobs.interview.coupon.core.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.exceptions.InvalidDiscountException;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @BeforeEach
    public void beforeEach() {
        when(couponRepository.findByCode("1111"))
                .thenReturn(Optional.of(Coupon.builder()
                        .code("1111")
                        .discount(BigDecimal.TEN)
                        .minBasketValue(BigDecimal.valueOf(50))
                        .build()));
    }

    @Test
    public void test_can_create_coupon() {
        var dto = CouponDTO.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();
        var savedCoupon = Mapper.toCoupon(dto);
        when(couponRepository.save(any())).thenReturn(savedCoupon);

        var result = couponService.createCoupon(Mapper.toCoupon(dto));

        verify(couponRepository, times(1)).save(any());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedCoupon);
    }

    @Test
    public void test_apply_coupon_successfully() {
        var basket = buildBasket(100);

        var optionalBasket = couponService.apply(basket, "1111");

        assertThat(optionalBasket).hasValueSatisfying(b -> {
            assertThat(b.getAppliedDiscount()).isEqualTo(BigDecimal.TEN);
            assertThat(b.isApplicationSuccessful()).isTrue();
        });
    }


    @Test
    public void test_cant_apply_coupon_of_basket_value_less_than_coupon_discount() {
        final var basket = buildBasket(10);

        var optionalBasket = couponService.apply(basket, "1111");

        assertThat(optionalBasket).hasValueSatisfying(b -> {
            assertThat(b).isEqualTo(basket);
            assertThat(b.isApplicationSuccessful()).isFalse();
        });
    }

    @Test
    public void test_throws_exception_for_negative_coupon_discount() {
        var basket = buildBasket(100);
        when(couponRepository.findByCode("2222"))
                .thenReturn(Optional.of(Coupon.builder()
                        .code("1111")
                        .discount(BigDecimal.valueOf(-1))
                        .minBasketValue(BigDecimal.valueOf(50))
                        .build()));

        assertThatThrownBy(() -> couponService.apply(basket, "2222"))
                .isInstanceOf(InvalidDiscountException.class)
                .hasMessage("Discount cannot be negative");
    }

    @Test
    public void test_can_get_coupon_list() {
        var coupons = List.of("1111", "1234");
        when(couponRepository.findByCodeIn(any()))
                .thenReturn(Set.of(
                        Coupon.builder()
                                .code("1111")
                                .discount(BigDecimal.TEN)
                                .minBasketValue(BigDecimal.valueOf(50)).build(),
                        Coupon.builder()
                                .code("1234")
                                .discount(BigDecimal.TEN)
                                .minBasketValue(BigDecimal.valueOf(50)).build())
                );

        var returnedCoupons = new ArrayList<>(couponService.findCouponsByCodes(coupons));

        assertThat(returnedCoupons.get(0).getCode()).isEqualTo("1111");
        assertThat(returnedCoupons.get(1).getCode()).isEqualTo("1234");
    }

    private static Basket buildBasket(int value) {
        return Basket.builder()
                .value(BigDecimal.valueOf(value))
                .build();
    }
}
