package pw.komarov.aeonpay.data.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pw.komarov.aeonpay.data.users.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "PAYMENTS")
@NoArgsConstructor
@RequiredArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Column(name = "COMPLETED_AT", updatable = false, insertable = false, nullable = false
            ,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()"
    )
    @Getter @Setter private Date date;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "FROM_USER_ID", updatable = false, nullable = false)
    @JsonIgnore
    @NonNull @Getter @Setter private User user;

    @Column(updatable = false, nullable = false, precision = 21, scale = 2)
    @NonNull @Getter @Setter private BigDecimal amount;
}
