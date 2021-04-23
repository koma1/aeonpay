package pw.komarov.aeonpay.security.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final RequestMatcher URL_ZONE_PUBLIC = new OrRequestMatcher(
            new AntPathRequestMatcher("/h2-console/**"),
            new AndRequestMatcher(
                    new AntPathRequestMatcher("/login"),
                    httpServletRequest -> httpServletRequest.getMethod().equalsIgnoreCase("POST")
            )
    );
    private static final RequestMatcher URL_ZONE_AUTHENTICATED = new NegatedRequestMatcher(URL_ZONE_PUBLIC);

    @Autowired
    private TokenAuthenticationProvider tokenAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .headers().frameOptions().disable()
                    .and()
                .logout().disable()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .authorizeRequests()
                    .requestMatchers(URL_ZONE_AUTHENTICATED).authenticated()
                    .anyRequest().permitAll()
        .and()
                .exceptionHandling().defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(UNAUTHORIZED),
                URL_ZONE_AUTHENTICATED)
        .and()
                .authenticationProvider(tokenAuthenticationProvider)
                .addFilterBefore(restAuthenticationFilter(), AnonymousAuthenticationFilter.class)
        ;
    }

    TokenAuthenticationFilter restAuthenticationFilter() throws Exception {
        final TokenAuthenticationFilter filter = new TokenAuthenticationFilter(URL_ZONE_AUTHENTICATED);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successHandler());

        return filter;
    }

    SimpleUrlAuthenticationSuccessHandler successHandler() {
        final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy(new DisabledRedirectStrategy());

        return successHandler;
    }
}
