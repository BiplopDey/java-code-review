package schwarz.jobs.interview.coupon.web;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.Mapper;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class CouponResource {

    private final CouponService couponService;

    @PostMapping(value = "/apply")
    @Operation(summary = "Applies active promotions and coupons to the given basket",
            description = "This endpoint applies any active coupons or promotions to the provided basket, " +
                    "based on the given coupon code and basket details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon successfully applied to the basket",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Basket.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "Coupon application conflict")
    })
    public ResponseEntity<Basket> apply(
            @Parameter(description = "Request DTO containing the basket details and coupon code", required = true)
            @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {
        log.info("Applying coupon");
        return couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode())
                .map(basket -> basket.isApplicationSuccessful() ?
                        ResponseEntity.ok(basket) : ResponseEntity.status(HttpStatus.CONFLICT).<Basket>build())
                .orElseGet(() -> ResponseEntity.<Basket>notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Coupon> create(@RequestBody @Valid final CouponDTO couponDTO) {
        return couponService.createCoupon(Mapper.toCoupon(couponDTO))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PostMapping("/coupons")
    public ResponseEntity<Set<Coupon>> getCoupons(@RequestBody @Valid final CouponRequestDTO couponRequestDTO) {
        var codes = couponRequestDTO.getCodes();
        if (couponRequestDTO.getCodes().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var coupons = couponService.findCouponsByCodes(codes);
        return coupons.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(coupons);
    }
}
