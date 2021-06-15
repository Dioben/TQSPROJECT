package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDeliveryActionException extends Exception {

    public InvalidDeliveryActionException(String message) {
        super(message);
    }

    public InvalidDeliveryActionException() {
        super("Invalid delivery action");
    }

}