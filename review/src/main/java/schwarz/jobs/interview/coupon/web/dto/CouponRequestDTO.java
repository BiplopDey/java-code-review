package schwarz.jobs.interview.coupon.web.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class CouponRequestDTO {

    @NotEmpty(message = "Coupon codes list cannot be empty.")
    private List<String> codes;

}
