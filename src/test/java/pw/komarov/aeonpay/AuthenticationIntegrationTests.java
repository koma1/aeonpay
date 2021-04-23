package pw.komarov.aeonpay;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pw.komarov.aeonpay.utils.HttpUtils;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AeonPayApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthenticationIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Value("${default.user.login}")
    private String login;
    @Value("${default.user.password}")
    private String password;
    @Value("${application.session.tti}")
    private Integer sessionTTI;

    @Test
    public void test1_missedParamsLoginTest() throws Exception {
        //missed username parameter
        mockMvc.perform(
                post("/login")
                        .param("password", password) )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":{\"type\":\"BadRequestParameterException\",\"message\":\"Missed mandatory parameter: \\\"username\\\"\"}}"))
        ;
        //missed password parameter
        mockMvc.perform(
                post("/login")
                        .param("username", login) )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":{\"type\":\"BadRequestParameterException\",\"message\":\"Missed mandatory parameter: \\\"password\\\"\"}}"))
        ;
    }

    @Test
    public void test2_failedLoginTest() throws Exception {
        mockMvc.perform(
                post("/login")
                        .param("username", UUID.randomUUID().toString())
                        .param("password", UUID.randomUUID().toString()) )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"error\":{\"type\":\"AuthenticationException\",\"message\":\"Invalid username/password\"}}"))
        ;
    }

    private static UUID token;

    @Test
    public void test3_successLoginTest() throws Exception {
        token = Utils.makeLogin(login, password, mockMvc);
    }

    @Test
    public void test4_logoutTest() throws Exception {
        mockMvc.perform(
                get("/logout")
                        .header(
                                HttpUtils.HTTP_HEADER_AUTHORIZATION,
                                String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)) )
                .andExpect(status().isNoContent())
        ;

        mockMvc.perform(
                get("/logout")
                        .header(
                                HttpUtils.HTTP_HEADER_AUTHORIZATION,
                                String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)) )
                .andExpect(status().isUnauthorized())
        ;
    }

    @Test
    public void test5_sessionTimeoutTTI() throws Exception {
        UUID token = Utils.makeLogin(login, password, mockMvc);

        for (int i = 1; i <= 3; i++) { //session keep-alive signals
            Thread.sleep(30000);
            Utils.verifyToken(mockMvc, token, status().isOk());
        }

        //wait for session TTI exceed
        Thread.sleep(1000L * 60 * sessionTTI + 30000);

        Utils.verifyToken(mockMvc, token, status().isUnauthorized());
    }
}