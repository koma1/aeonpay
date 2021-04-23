package pw.komarov.aeonpay;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pw.komarov.aeonpay.data.payments.InsufficientFundsException;
import pw.komarov.aeonpay.data.payments.PaymentService;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.data.users.UserService;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = AeonPayApplication.class)
@AutoConfigureMockMvc
public class DaoPaymentTests {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserService userService;

    @Value("${application.newuser.balance}")
    private BigDecimal initialBalance;
    @Value("${application.withdraw.amount}")
    private BigDecimal withdrawAmount;

    @Test
    public void paymentTest() throws Exception {
        final int threadsCount = Utils.calcThreadsCount(initialBalance, withdrawAmount);
        User user = userService.getFirstBy().orElseThrow(RuntimeException::new);

        AtomicReference<Exception> refException = new AtomicReference<>();
        Utils.parallelExecute(() -> {
                if (refException.get() == null)
                    try {
                        paymentService.makePayment(user, withdrawAmount);
                    } catch (InsufficientFundsException insufficientFundsException) {
                        refException.set(insufficientFundsException);
                    }
        }, threadsCount);
        Assertions.assertNull(refException.get());

        Assertions.assertEquals(
                initialBalance.subtract(withdrawAmount.multiply(BigDecimal.valueOf(threadsCount))).doubleValue(), //initial - (withdraw * threadsCount)
                user.getBalance().doubleValue());
    }
}