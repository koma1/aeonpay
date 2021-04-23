package pw.komarov.aeonpay.security.antibrute;

import org.springframework.security.core.AuthenticationException;

public class TooManyFailedAuthenticationAttemptsException extends AuthenticationException {
    public TooManyFailedAuthenticationAttemptsException(String message) {
        super(message);
    }
}
