package schwarz.jobs.interview.coupon.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwarz.jobs.interview.coupon.core.domain.Coupon;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(final String code);

    Set<Coupon> findByCodeIn(List<String> codes);
}
