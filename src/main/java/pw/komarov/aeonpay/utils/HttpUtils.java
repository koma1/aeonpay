package pw.komarov.aeonpay.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.*;

public final class HttpUtils {
    public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_HEADER_BEARER_PREFIX = "Bearer";

    public static Optional<UUID> extractBearerTokenFromHttpRequest(HttpServletRequest request) {
        String token = trim(removeStart(request.getHeader(HTTP_HEADER_AUTHORIZATION), AUTHORIZATION_HEADER_BEARER_PREFIX));
        return Optional.ofNullable(isNotEmpty(token) ? UUID.fromString(token) : null);
    }
}
