package be.kdg.repository;

import be.kdg.domain.Nfc;
import be.kdg.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NfcRepository extends JpaRepository<Nfc, Long> {

    Optional<Nfc> findByCredentials(String credentials);

    long count();

    long countByUserId(Long userId);

    List<Nfc> findByUserId(Long userId);
    
    
}
