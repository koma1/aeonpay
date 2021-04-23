package pw.komarov.aeonpay.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pw.komarov.aeonpay.data.payments.InsufficientFundsException;
import pw.komarov.aeonpay.rest.exceptions.BadRequestParameterException;
import pw.komarov.aeonpay.rest.exceptions.InternalErrorException;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestParameterException.class)
    public ResponseEntity<ExceptionWrapper> makeResponse(BadRequestParameterException e) {
        return new ResponseEntity<>(new ExceptionWrapper(e),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ExceptionWrapper> makeResponse(AuthenticationException e) {
        return new ResponseEntity<>(new ExceptionWrapper(
                AuthenticationException.class,
                "Invalid username/password"),
            HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ExceptionWrapper> makeResponseAuth(AuthenticationException e) {
        return new ResponseEntity<>(new ExceptionWrapper(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({InternalErrorException.class})
    public ResponseEntity<ExceptionWrapper> makeResponse(InternalErrorException e) {
        return new ResponseEntity<>(new ExceptionWrapper(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InsufficientFundsException.class})
    public ResponseEntity<ExceptionWrapper> makeResponse(InsufficientFundsException e) {
        return new ResponseEntity<>(new ExceptionWrapper(e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({Exception.class}) //ATTENTION: <- unhandled (must be logged in future)!
    public ResponseEntity<ExceptionWrapper> makeResponseAll(Exception e) {
        return new ResponseEntity<>(new ExceptionWrapper(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
