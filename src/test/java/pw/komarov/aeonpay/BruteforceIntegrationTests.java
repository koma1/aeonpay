package pw.komarov.aeonpay;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AeonPayApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
public class BruteforceIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Value("${default.user.login}")
    private String login;
    @Value("${default.user.password}")
    private String password;
    @Value("${application.antibrute.attempts}")
    private Integer attempts;
    @Value("${application.antibrute.ttl}")
    private Integer lockTTL;

    private static boolean prepared; //for @BeforeClass non-static simulation

    @Before
    public void prepare() throws Exception {
        if (!prepared) {
            for (int i = 1; i <= attempts; i++)
                mockMvc.perform(
                        post("/login")
                                .param("username", login)
                                .param("password", UUID.randomUUID().toString()))
                        .andExpect(status().isUnauthorized())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().json("{\"error\":{\"type\":\"AuthenticationException\",\"message\":\"Invalid username/password\"}}"))
                ;

            prepared = true;
        }
    }

    @Test
    public void test1_loginLocked_bruteforceProtectionTest() throws Exception {
        mockMvc.perform(
                post("/login")
                        .param("username", login)
                        .param("password", UUID.randomUUID().toString()) )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":{\"type\":\"TooManyFailedAuthenticationAttemptsException\",\"message\":\"Login locked\"}}"))
        ;
    }

    @Test
    public void test2_addressLocked_bruteforceProtectionTest() throws Exception {
        mockMvc.perform(
                post("/login")
                        .param("username", UUID.randomUUID().toString())
                        .param("password", UUID.randomUUID().toString()) )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":{\"type\":\"TooManyFailedAuthenticationAttemptsException\",\"message\":\"Address locked\"}}"))
        ;
    }

    @Test
    public void test3_lockTimeoutExceed() throws Exception {
        Thread.sleep(lockTTL * 1000 * 60 + 30000); //waiting while lock timeout exceed
        Utils.makeLogin(login, password, mockMvc);
    }
}
