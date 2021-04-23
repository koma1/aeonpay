package pw.komarov.aeonpay.security.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.security.authentication.BadTokenException;
import pw.komarov.aeonpay.security.authentication.UserAuthenticationService;

import java.util.ArrayList;
import java.util.UUID;

@Configuration
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private UserAuthenticationService userService;

    @Override
    protected UserDetails retrieveUser(String token,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
        throws AuthenticationException {
            UUID uuid = UUID.fromString(token);
            User user = userService.findByToken(uuid).orElseThrow(BadTokenException::new);

            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(), user.getPassword(), new ArrayList<>());
        }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {}
}
