package pw.komarov.aeonpay.data.users;

import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserService {
    void save(@NonNull User user);

    Optional<User> findByLogin(@NonNull String login);
    Optional<User> getFirstBy();

    Iterable<User> findAll();

    User currentUser();

    Optional<User> findById(Long id);

    Optional<BigDecimal> getUserBalanceWithPessimisticWriteLock(Long userId);
}
