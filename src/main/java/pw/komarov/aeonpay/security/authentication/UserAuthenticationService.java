package pw.komarov.aeonpay.security.authentication;

import pw.komarov.aeonpay.data.users.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

public interface UserAuthenticationService {
    Optional<User> findByToken(UUID token);
    UUID login(String username, String password, HttpServletRequest request);

    void logout(UUID token);
}