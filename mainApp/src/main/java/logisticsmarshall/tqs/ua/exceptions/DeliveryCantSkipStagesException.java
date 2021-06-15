package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeliveryCantSkipStagesException extends Exception {

    public DeliveryCantSkipStagesException(String message) {
        super(message);
    }

    public DeliveryCantSkipStagesException() {
        super("Can't skip delivery stages");
    }

}