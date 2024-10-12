package schwarz.jobs.interview.coupon.core.services.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DecimalsUtil {

    public static boolean isNegative(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public static boolean isGreaterOrEqual(BigDecimal first, BigDecimal second) {
        return first.compareTo(second) >= 0;
    }

}
