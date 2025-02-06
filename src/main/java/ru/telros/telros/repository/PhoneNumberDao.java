package ru.telros.telros.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.telros.telros.model.PhoneNumber;

import java.util.Optional;

public interface PhoneNumberDao extends JpaRepository<PhoneNumber, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<PhoneNumber> findPhoneNumberByPhoneNumber(String phoneNumber);
}
