package pw.komarov.aeonpay.security.antibrute;

import javax.servlet.http.HttpServletRequest;

public interface AntibruteService extends UsernameHttpAuthenticationEventsListener {
    void checkAuthenticationAttempt(String username, HttpServletRequest request)
            throws TooManyFailedAuthenticationAttemptsException;
}
