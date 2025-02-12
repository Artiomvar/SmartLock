package be.kdg.service;

import be.kdg.domain.Log;
import be.kdg.dto.UserLastEntryDto;
import be.kdg.dto.UserLastEntryOwnDto;
import be.kdg.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public interface LogService {
    void saveLog(Log log);

    //this is for admin
    List<UserLastEntryDto> getLastEntryTimes();

    List<UserLastEntryOwnDto> getLastEntryByUserId(Long userId);

    List<Object[]> getWeeklyLoginStats(long userId);

    List<Object[]> getWeeklyLoginStatsAllUsers();

    List<Object[]> findLogsGroupedByDaytimeForSpecificUser(long userId);

    List<Object[]> findLogsGroupedByDaytimeForAllUsers(long userId);
}
