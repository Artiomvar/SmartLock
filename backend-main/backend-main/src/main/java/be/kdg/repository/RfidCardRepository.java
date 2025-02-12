package be.kdg.repository;

import be.kdg.domain.RfidCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RfidCardRepository extends JpaRepository<RfidCard, Long> {

    Optional<RfidCard> findByCredentials(String credentials);

    long count();
    long countByUserId(Long userId);

    List<RfidCard> findByUserId(Long userId);
     
     
}
