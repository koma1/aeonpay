package pw.komarov.aeonpay.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pw.komarov.aeonpay.data.payments.InsufficientFundsException;
import pw.komarov.aeonpay.data.payments.PaymentService;

import java.math.BigDecimal;

@RestController
public class PaymentsController {
    @Value("${application.withdraw.amount:1.1}")
    private BigDecimal withdrawAmount;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/payment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void payment() throws InsufficientFundsException {
        paymentService.makePayment(withdrawAmount);
    }
}
