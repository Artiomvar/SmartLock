package be.kdg.repository;

import be.kdg.domain.Sensor;
import be.kdg.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SensorRepository  extends JpaRepository<Sensor, Integer> {
    Sensor findByName(String name);
    Optional<Sensor> findByMac(String mac);
    long count();

    @Query("SELECT COUNT(s) FROM Sensor s JOIN s.authorizedUsers u WHERE u.id = :userId")
    long countSensorsByUser(@Param("userId") Long userId);

    @Query("SELECT s FROM Sensor s JOIN s.authorizedUsers u WHERE u.id = :userId")
    List<Sensor> findSensorsByUserId(@Param("userId") Long userId);
}
