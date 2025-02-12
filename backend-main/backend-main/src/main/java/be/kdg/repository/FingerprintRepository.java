package be.kdg.repository;

import be.kdg.domain.Fingerprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Long> {

    Optional<Fingerprint> findByCredentials(String credentials);

    long countByUserId(Long userId);

    List<Fingerprint> findByUserId(Long userId);

    long count();
    
}
