package schwarz.jobs.interview.coupon.core.services.exceptions;

public class InvalidDiscountException extends IllegalArgumentException {
    public InvalidDiscountException(String message) {
        super(message);
    }
}
