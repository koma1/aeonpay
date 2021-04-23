package pw.komarov.aeonpay.data.users;

import lombok.*;
import pw.komarov.aeonpay.data.payments.Payment;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "USERS")
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 25)
    @Getter @Setter @NonNull private String login;

    @Column(name = "PASSWORD_BCRYPT", length = 60)
    @Getter @Setter @NonNull private String password;

    @Column(name = "FULL_NAME", nullable = false)
    @Getter @Setter @NonNull private String fullName;

    @Column(nullable = false, precision = 21, scale = 2)
    @Getter @Setter @NonNull private BigDecimal balance;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public void setPayments(List<Payment> payments) {
        throw new UnsupportedOperationException();
    }
}
