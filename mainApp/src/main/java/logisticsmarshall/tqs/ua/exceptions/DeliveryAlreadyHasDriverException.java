package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeliveryAlreadyHasDriverException extends Exception {

    public DeliveryAlreadyHasDriverException(String message) {
        super(message);
    }

    public DeliveryAlreadyHasDriverException() {
        super("Delivery is already in progress/done");
    }

}
