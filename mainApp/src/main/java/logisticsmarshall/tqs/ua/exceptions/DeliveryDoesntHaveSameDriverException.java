package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeliveryDoesntHaveSameDriverException extends Exception {

    public DeliveryDoesntHaveSameDriverException(String message) {
        super(message);
    }

    public DeliveryDoesntHaveSameDriverException() {
        super("Delivery is already in progress/done");
    }

}
