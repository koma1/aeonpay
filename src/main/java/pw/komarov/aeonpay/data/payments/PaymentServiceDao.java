package pw.komarov.aeonpay.data.payments;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.komarov.aeonpay.data.users.User;
import pw.komarov.aeonpay.data.users.UserService;
import pw.komarov.aeonpay.rest.exceptions.InternalErrorException;

import java.math.BigDecimal;

@Service
public class PaymentServiceDao implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = InsufficientFundsException.class)
    public void makePayment(@NonNull BigDecimal amount) throws InsufficientFundsException {
        makePayment(userService.currentUser(), amount);
    }

    @Override
    @Transactional(rollbackFor = InsufficientFundsException.class)
    public void makePayment(@NonNull User userFrom, @NonNull BigDecimal amount) throws InsufficientFundsException {
        BigDecimal balance =
                userService.getUserBalanceWithPessimisticWriteLock(userFrom.getId())
                        .orElseThrow(() -> new InternalErrorException(
                                String.format("PaymentServiceDao.makePayment() - user id not found: %s",
                                        userFrom.getId().toString())));

        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.valueOf(0)) < 0)
            throw new InsufficientFundsException(String.format(
                    "Unable to withdraw %s, balance: %s", amount, balance));

        userFrom.setBalance(newBalance);

        userService.save(userFrom);
        paymentRepository.save(new Payment(userFrom, amount));

        //long wait operation simulation (for locking test)
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
//        try { Thread.sleep(new Random().nextInt(2000) + 2000); } catch (InterruptedException ignored) {}
    }
}
