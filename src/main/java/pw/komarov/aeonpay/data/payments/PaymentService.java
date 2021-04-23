package pw.komarov.aeonpay.data.payments;

import lombok.NonNull;
import pw.komarov.aeonpay.data.users.User;

import java.math.BigDecimal;

public interface PaymentService {
    void makePayment(@NonNull BigDecimal amount) throws InsufficientFundsException;
    void makePayment(@NonNull User userFrom, @NonNull BigDecimal amount) throws InsufficientFundsException;
}
