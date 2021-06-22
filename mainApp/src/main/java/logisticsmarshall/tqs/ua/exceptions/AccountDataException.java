package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountDataException extends Exception {

    public AccountDataException(String message) {
        super(message);
    }

    public AccountDataException() {
        super("Invalid account data");
    }

}