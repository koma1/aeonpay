package pw.komarov.aeonpay.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class BadTokenException extends AuthenticationException {
    public BadTokenException(String msg) {
        super(msg);
    }

    public BadTokenException() {
        this("Bad token!");
    }
}
