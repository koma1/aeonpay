package pw.komarov.aeonpay.security.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoders {
    @Bean
    PasswordEncoder passwordEncoder() {
//        return new PlainTextPasswordEncoder();
        return new BCryptPasswordEncoder();
    }

    public static final class PlainTextPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence charSequence) {
            return charSequence.toString();
        }

        @Override
        public boolean matches(CharSequence charSequence, String s) {
            return charSequence.toString().equals(s);
        }
    }
}
