package pw.komarov.aeonpay.data.users;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pw.komarov.aeonpay.rest.exceptions.InternalErrorException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserServiceDao implements UserService {
    @Autowired
    private UserRepository repository;

    @Override
    public void save(@NonNull User user) {
        repository.save(user);
    }

    @Override
    public Optional<User> findByLogin(@NonNull String login) {
        return repository.findByLogin(login);
    }

    @Override
    public Optional<User> getFirstBy() {
        return repository.getFirstBy();
    }

    @Override
    public Optional<BigDecimal> getUserBalanceWithPessimisticWriteLock(Long userId) {
        return repository.getUserBalanceWithPessimisticWriteLock(userId);
    }

    @Override
    public Iterable<User> findAll() {
        return repository.findAll();
    }

    @Override
    public User currentUser() {
        String username = ((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return findByLogin(username)
                .orElseThrow(() -> new InternalErrorException(
                        String.format("UserServiceDao.currentUser() - login not found: %s", username))
                );
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
}
