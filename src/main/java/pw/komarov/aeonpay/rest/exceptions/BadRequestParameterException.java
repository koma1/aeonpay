package pw.komarov.aeonpay.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestParameterException extends RuntimeException {
    public BadRequestParameterException(String missedParameterName) {
        super(String.format("Missed mandatory parameter: \"%s\"", missedParameterName));
    }

    public BadRequestParameterException(String mismatchedParameterName, String mismatchedParameterValue) {
        super(String.format("Parameter \"%s\", have invalid value \"%s\"!",
                mismatchedParameterName, mismatchedParameterValue));
    }
}
