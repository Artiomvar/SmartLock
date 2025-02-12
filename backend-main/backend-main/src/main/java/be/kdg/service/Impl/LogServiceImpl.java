package be.kdg.service.Impl;

import be.kdg.domain.Log;
import be.kdg.domain.User;
import be.kdg.dto.UserLastEntryDto;
import be.kdg.dto.UserLastEntryOwnDto;
import be.kdg.repository.LogRepository;
import be.kdg.repository.UserRepository;
import be.kdg.service.LogService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final UserRepository userRepository;

    public LogServiceImpl(LogRepository logRepository, UserRepository userRepository) {
        this.logRepository = logRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveLog(Log log) {
        logRepository.save(log);
    }


    //this is for admin all users last time entry.
    @Override
    public List<UserLastEntryDto> getLastEntryTimes() {
        List<Object[]> results = logRepository.findLastEntryTimeForAllUsers();
        List<UserLastEntryDto> userEntries = new ArrayList<>();

        for (Object[] result : results) {
            Long userId = (Long) result[0];
            String lastEntryTime = result[1].toString();

            // Fetch username by userId
            User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
            if (user != null) {
                UserLastEntryDto dto = new UserLastEntryDto(userId, user.getUsername(), lastEntryTime);
                userEntries.add(dto);
            }
        }

        return userEntries;
    }

    @Override
    public List<UserLastEntryOwnDto> getLastEntryByUserId(Long userId) {
        return logRepository.findLast5LogsByUserId(userId);
    }


    @Override
    public List<Object[]> getWeeklyLoginStats(long userId) {
        List<Object[]> results = logRepository.findWeeklyLoginsByDay(userId);
        return results;
    }

    @Override
    public List<Object[]> getWeeklyLoginStatsAllUsers() {
        List<Object[]> results = logRepository.findWeeklyLoginsByDayForAllUsers();
        return results;
    }

    @Override
    public List<Object[]> findLogsGroupedByDaytimeForSpecificUser(long userId) {
        return logRepository.findLogsGroupedByDaytimeForSpecificUser(userId);
    }

    @Override
    public List<Object[]> findLogsGroupedByDaytimeForAllUsers(long userId) {
        return logRepository.findLogsGroupedByDaytimeForSpecificUser(userId);
    }
    
    
}
