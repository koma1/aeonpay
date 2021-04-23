package pw.komarov.aeonpay.security.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class DisabledRedirectStrategy implements RedirectStrategy {
    @Override
    public void sendRedirect(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            String url) {
    }
}
