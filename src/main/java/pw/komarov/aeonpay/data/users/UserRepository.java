package pw.komarov.aeonpay.data.users;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByLogin(String login);
    Optional<User> getFirstBy();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u.balance from User u where u.id = ?1")
    Optional<BigDecimal> getUserBalanceWithPessimisticWriteLock(Long userId);
}
