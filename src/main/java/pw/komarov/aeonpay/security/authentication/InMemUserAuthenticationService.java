package pw.komarov.aeonpay.security.authentication;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.expiry.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.data.users.UserService;
import pw.komarov.aeonpay.security.antibrute.AntibruteService;
import pw.komarov.aeonpay.utils.CacheUtils;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class InMemUserAuthenticationService implements UserAuthenticationService {
    @Autowired
    private UserService userService;

    @Autowired
    private AntibruteService antibruteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CacheManager cacheManager = CacheUtils.createCacheManager();

    @SuppressWarnings("rawtypes")
    private final Cache userTokens;

    public InMemUserAuthenticationService(
            @Value("${application.session.tti:20}") Integer sessionsTTI,
            @Value("${application.session.cache.heap.size:500}") Integer heapSize
    ) {
        userTokens =
                CacheUtils.createObjectsHeapCache(
                        cacheManager,
                        "tokens",
                        heapSize,
                        null,
                        Duration.of(sessionsTTI, TimeUnit.MINUTES));
    }

    @Override
    public Optional<User> findByToken(UUID token) {
        //noinspection unchecked
        return Optional.ofNullable((User) userTokens.get(token));
    }

    @Override
    public UUID login(String username, String presentedPassword, HttpServletRequest request) {
        antibruteService.checkAuthenticationAttempt(username, request);

        User user;
        try {
            user = userService.findByLogin(username).orElseThrow(() ->
                    new UsernameNotFoundException(String.format("User not found by login %s", username)));

            if (!passwordEncoder.matches(presentedPassword, user.getPassword()))
                throw new BadCredentialsException("Password incorrect");
        } catch (AuthenticationException e) {
            antibruteService.failedAuthentication(username, request, e);

            throw e;
        }

        antibruteService.successAuthentication(username, request);

        UUID uuid = UUID.randomUUID();
        //noinspection unchecked
        userTokens.put(uuid, user);

        return uuid;
    }

    @Override
    public void logout(UUID token) {
        //noinspection unchecked
        userTokens.remove(token);
    }

    @PreDestroy
    private void closeCacheManager() {
        cacheManager.close();
    }
}