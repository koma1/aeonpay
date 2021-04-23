package pw.komarov.aeonpay.security.antibrute;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.expiry.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import pw.komarov.aeonpay.utils.CacheUtils;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class CacheBasedAntibruteService implements AntibruteService {
    @Value("${application.antibrute.attempts:3}")
    private Integer attempts;
    private final Integer lockTTL;
    private final Integer heapSize;

    private final CacheManager cacheManager = CacheUtils.createCacheManager();
    private final Cache<String, Integer> loginsCache;
    private final Cache<String, Integer> addressesCache;

    private Cache<String, Integer> createCache(String name) {
        return CacheUtils.createStringIntegerHeapCache(
                cacheManager, name, heapSize, Duration.of(lockTTL, TimeUnit.MINUTES), null
        );
    }

    public CacheBasedAntibruteService(
            @Value("${application.antibrute.ttl:1}") Integer lockTTL,
            @Value("${application.antibrute.cache.heap.size:500}") Integer heapSize) {
        this.lockTTL = lockTTL;
        this.heapSize = heapSize;

        addressesCache = createCache("antibruteAddresses");
        loginsCache = createCache("antibruteLogins");
    }

    @Override
    public void successAuthentication(String username, HttpServletRequest request) {
        //тут сбрасываем только счетчик неудачных входов по логину, т.к. злоумышленник может использовать один
        //          корректный вход для очистки своей истории bad login'ов и дальше брутить логин к паролю
        loginsCache.remove(username);
    }

    @Override
    public void failedAuthentication(String username, HttpServletRequest request, AuthenticationException e) {
        if (e instanceof BadCredentialsException) //invalid password
            increaseCount(loginsCache, username);

        increaseCount(addressesCache, request.getRemoteAddr());
    }

    @Override
    public synchronized void checkAuthenticationAttempt(String username, HttpServletRequest request)
            throws TooManyFailedAuthenticationAttemptsException {

        String address = request.getRemoteAddr();

        int logins = loginsCache.containsKey(username) ? loginsCache.get(username) : 0;
        int addresses = addressesCache.containsKey(address) ? addressesCache.get(address) : 0;

        if (logins >= attempts)
            throw new TooManyFailedAuthenticationAttemptsException("Login locked");

        if (addresses >= attempts)
            throw new TooManyFailedAuthenticationAttemptsException("Address locked");
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private void increaseCount(Cache<String,Integer> cache, String key) {
        synchronized (cache) {
            if (!cache.containsKey(key))
                cache.put(key, 0);

            cache.replace(key, cache.get(key) + 1);
        }
    }

    @PreDestroy
    public void destroy() {
        cacheManager.close();
    }
}
