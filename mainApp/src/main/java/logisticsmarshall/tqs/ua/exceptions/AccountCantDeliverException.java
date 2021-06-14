package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountCantDeliverException extends Exception {

    public AccountCantDeliverException(String message) {
        super(message);
    }

    public AccountCantDeliverException() {
        super("Account can't make this delivery");
    }

}
