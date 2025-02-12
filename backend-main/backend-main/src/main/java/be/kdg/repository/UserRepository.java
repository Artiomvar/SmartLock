package be.kdg.repository;

import be.kdg.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    long count();
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.userDetails WHERE u.id = :id")
    Optional<User> findUserWithDetailsById(@Param("id") Long id);


    @Query("SELECT u FROM User u ORDER BY u.id ASC")
    List<User> findAllOrderById();

    @Query("SELECT u.userType AS userType, COUNT(u) * 1.0 / (SELECT COUNT(u) FROM User u) * 100 AS percentage " +
           "FROM User u GROUP BY u.userType")
    List<Object[]> findUserTypePercentages();


    @Query("SELECT ud.country FROM User u JOIN u.userDetails ud")
    List<String> findAllUsersWithCountries();

}
