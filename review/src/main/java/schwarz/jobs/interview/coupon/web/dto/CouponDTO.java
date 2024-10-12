package schwarz.jobs.interview.coupon.web.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class CouponDTO {

    @NotNull(message = "Discount cannot be null")
    @DecimalMin(value = "0.01", message = "Discount must be greater than 0")
    private BigDecimal discount;

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotNull(message = "Minimum basket value cannot be null")
    @DecimalMin(value = "0.00", message = "Minimum basket value cannot be negative")
    private BigDecimal minBasketValue;

}
