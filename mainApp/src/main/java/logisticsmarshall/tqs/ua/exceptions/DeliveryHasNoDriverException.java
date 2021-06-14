package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeliveryHasNoDriverException extends Exception {

    public DeliveryHasNoDriverException(String message) {
        super(message);
    }

    public DeliveryHasNoDriverException() {
        super("Delivery is not in progress");
    }

}
