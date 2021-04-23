package pw.komarov.aeonpay;

import org.junit.Assert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import pw.komarov.aeonpay.utils.HttpUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class Utils {
    public static UUID makeLogin(final String login, final String password, final MockMvc mockMvc) throws Exception {
        String tokenStr = mockMvc.perform(
                post("/login")
                        .param("username", login)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return UUID.fromString(tokenStr.substring(1, tokenStr.length() - 1));
    }

    public static void verifyToken(MockMvc mockMvc, UUID token, ResultMatcher expect) throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/users/current")
                    .header(
                            HttpUtils.HTTP_HEADER_AUTHORIZATION,
                            String.format("%s %s", HttpUtils.AUTHORIZATION_HEADER_BEARER_PREFIX, token)));

        resultActions.andExpect(expect);
    }

    public static void parallelExecute(final Runnable runnable, final int count) throws AssertionError, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(count);
        for (int i = 1; i <= count; i++)
            es.execute(runnable);
        es.shutdown();

        Assert.assertTrue(es.awaitTermination(1, TimeUnit.MINUTES));
    }

    static int calcThreadsCount(final BigDecimal initialBalance, final BigDecimal withdrawAmount) {
        return initialBalance
                .divide(withdrawAmount, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.FLOOR)
                .intValue();
    }
}
