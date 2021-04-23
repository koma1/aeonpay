package pw.komarov.aeonpay;

import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pw.komarov.aeonpay.data.payments.InsufficientFundsException;
import pw.komarov.aeonpay.utils.HttpUtils;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AeonPayApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PaymentsIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Value("${default.user.login}")
    private String login;
    @Value("${default.user.password}")
    private String password;
    @Value("${application.newuser.balance}")
    private BigDecimal initialBalance;
    @Value("${application.withdraw.amount}")
    private BigDecimal withdrawAmount;

    private static UUID token;
    private static int paymentExecutedCount;

    @Before
    public void initLogin() throws Exception {
        if (token == null)
            token = Utils.makeLogin(login, password, mockMvc);

        Assert.assertNotNull(token);
    }

    private void executePayment() throws Exception {
        paymentExecutedCount++;

        mockMvc.perform(
                get("/payment")
                        .header(
                                HttpUtils.HTTP_HEADER_AUTHORIZATION,
                                String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)) )
                .andExpect(status().isNoContent())
        ;
    }

    private void verifyPaymentDetails() throws Exception {
        double expectedBalance =
                initialBalance.subtract(
                    withdrawAmount.multiply(
                        BigDecimal.valueOf(paymentExecutedCount))).doubleValue();

        int expectedDetailsCount = paymentExecutedCount;

        mockMvc.perform(
                get("/users/current")
                        .header(
                                HttpUtils.HTTP_HEADER_AUTHORIZATION,
                                String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)) )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(expectedBalance)))
                .andExpect(jsonPath("$.payments", hasSize(expectedDetailsCount)))
        ;
    }

    @Test
    public void test1_withdrawRoundingTest() throws Exception {
        executePayment();

        verifyPaymentDetails();
    }

    @Test
    public void test2_transactionsIsolationTest() throws Exception {
        final int threadsCount = Utils.calcThreadsCount(initialBalance, withdrawAmount) - paymentExecutedCount;

        AtomicReference<Exception> refException = new AtomicReference<>();
        Utils.parallelExecute(() -> {
            if (refException.get() == null)
                try {
                    executePayment();
                } catch (Exception e) {
                    refException.set(e);
                }
        }, threadsCount);
        Assertions.assertNull(refException.get());

        verifyPaymentDetails();
    }

    @Test
    public void test3_InsufficientFundsTest() throws Exception {
        mockMvc.perform(
                get("/payment")
                        .header(
                                HttpUtils.HTTP_HEADER_AUTHORIZATION,
                                String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)) )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.type", is(InsufficientFundsException.class.getSimpleName())))
                .andExpect(jsonPath("$.error.message", containsStringIgnoringCase("Unable to withdraw ")))
                .andExpect(jsonPath("$.error.message", containsStringIgnoringCase("balance: ")))
        ;
    }
}