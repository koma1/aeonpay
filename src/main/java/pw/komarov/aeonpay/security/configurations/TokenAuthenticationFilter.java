package pw.komarov.aeonpay.security.configurations;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import pw.komarov.aeonpay.utils.HttpUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public TokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws AuthenticationException {

        UUID token = HttpUtils.extractBearerTokenFromHttpRequest(httpServletRequest)
                .orElseThrow(() -> new BadCredentialsException("Missing authentication token"));

        final Authentication authentication = new UsernamePasswordAuthenticationToken(token, token);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain,
            final Authentication authResult) throws IOException, ServletException {

        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
