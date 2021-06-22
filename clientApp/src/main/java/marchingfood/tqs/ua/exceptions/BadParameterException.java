package marchingfood.tqs.ua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadParameterException extends Exception {

    public BadParameterException(String s) {
        super(s);
    }
}
