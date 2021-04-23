package pw.komarov.aeonpay.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pw.komarov.aeonpay.rest.exceptions.BadRequestParameterException;
import pw.komarov.aeonpay.security.authentication.UserAuthenticationService;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static pw.komarov.aeonpay.utils.HttpUtils.*;

@RestController
public class AuthenticationController {
    @PostMapping("/login")
    public UUID login(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password, HttpServletRequest request) {

        if (username == null)
            throw new BadRequestParameterException("username");
        if (password == null)
            throw new BadRequestParameterException("password");

        return userAuthenticationService.login(username, password, request);
    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest req) {
        UUID token = extractBearerTokenFromHttpRequest(req)
                .orElseThrow(() -> new RuntimeException("missing token"));

        userAuthenticationService.logout(token);
    }

    @Autowired
    private UserAuthenticationService userAuthenticationService;
}
