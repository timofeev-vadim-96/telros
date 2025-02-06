package ru.telros.telros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telros.telros.model.User;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
