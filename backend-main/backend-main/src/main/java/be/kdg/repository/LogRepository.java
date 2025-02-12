package be.kdg.repository;

import be.kdg.domain.Log;
import be.kdg.dto.UserLastEntryOwnDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {

    @Query("SELECT l.userId, MAX(l.timestamp) " +
            "FROM Log l " +
            "GROUP BY l.userId")
    List<Object[]> findLastEntryTimeForAllUsers();

    @Query("SELECT new be.kdg.dto.UserLastEntryOwnDto(l.userId, l.lockId, l.method, l.success, l.timestamp) " +
            "FROM Log l WHERE l.userId = :userId ORDER BY l.timestamp DESC")
    List<UserLastEntryOwnDto> findLast5LogsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT EXTRACT(DOW FROM l.timestamp) AS dayOfWeek, COUNT(l.id) AS loginsCount " +
            "FROM log l " +
            "WHERE l.user_id = :userId AND l.timestamp >= CURRENT_DATE - INTERVAL '7 days' " +
            "GROUP BY EXTRACT(DOW FROM l.timestamp) " +
            "ORDER BY EXTRACT(DOW FROM l.timestamp)", nativeQuery = true)
    List<Object[]> findWeeklyLoginsByDay(@Param("userId") Long userId);

    @Query(value = "SELECT EXTRACT(DOW FROM l.timestamp) AS dayOfWeek, COUNT(l.id) AS loginsCount " +
            "FROM log l " +
            "WHERE l.timestamp >= CURRENT_DATE - INTERVAL '7 days' " +
            "GROUP BY EXTRACT(DOW FROM l.timestamp) " +
            "ORDER BY EXTRACT(DOW FROM l.timestamp)", nativeQuery = true)
    List<Object[]> findWeeklyLoginsByDayForAllUsers();


    @Query(value = "SELECT " +
            "CASE " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 6 AND 11 THEN 'Morning' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 12 AND 17 THEN 'Afternoon' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 18 AND 23 THEN 'Evening' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 0 AND 5 THEN 'Night' " +
            "END AS daytime, " +
            "COUNT(l.id) AS loginsCount " +
            "FROM log l " +
            "WHERE l.user_id = :userId " +
            "GROUP BY " +
            "CASE " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 6 AND 11 THEN 'Morning' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 12 AND 17 THEN 'Afternoon' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 18 AND 23 THEN 'Evening' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 0 AND 5 THEN 'Night' " +
            "END " +
            "ORDER BY " +
            "FIELD(CASE " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 6 AND 11 THEN 'Morning' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 12 AND 17 THEN 'Afternoon' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 18 AND 23 THEN 'Evening' " +
            "    WHEN EXTRACT(HOUR FROM l.timestamp) BETWEEN 0 AND 5 THEN 'Night' " +
            "END, 'Morning', 'Afternoon', 'Evening', 'Night')",
            nativeQuery = true)
    List<Object[]> findLogsGroupedByDaytimeForSpecificUser(@Param("userId") long userId);
}
