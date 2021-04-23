package pw.komarov.aeonpay.security.antibrute;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

public interface UsernameHttpAuthenticationEventsListener {
    void successAuthentication(String username, HttpServletRequest request);
    void failedAuthentication(String username, HttpServletRequest request, AuthenticationException e);
}
