package pw.komarov.aeonpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.data.users.UserService;

import java.math.BigDecimal;

@SpringBootApplication
public class AeonPayApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(AeonPayApplication.class, args);
    }

    @Value("${default.user.login:sa}")
    private String login;
    @Value("${default.user.password:as}")
    private String password;
    @Value("${application.newuser.balance:8}")
    private BigDecimal startBalance;

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private void createUser(String login, String fullName) {
        userService.save(new User(login, passwordEncoder.encode(password), fullName, startBalance));
    }

    private void createUsers() {
        createUser(login, "Dow Jones");
        createUser("m.spens", "Mark Spenser");
        createUser("vika89", "Rodina Viktoria");
    }

    @Override
    public void run(ApplicationArguments args) {
        createUsers();


    }
}
