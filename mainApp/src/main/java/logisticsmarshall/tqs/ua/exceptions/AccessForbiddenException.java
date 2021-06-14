package logisticsmarshall.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class AccessForbiddenException extends Exception{
    public AccessForbiddenException(String message) {
        super(message);
    }

    public AccessForbiddenException() {
        super("Page Access Forbidden");
    }
}
