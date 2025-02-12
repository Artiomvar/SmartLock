package be.kdg.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;

public class UserLastEntryDto {
    // Getters and Setters
    @Getter
    private Long userId;
    @Getter
    private String username;

    @Getter
    private String lastEntryTime;

    @Getter
    private String periodOfDay;


    public UserLastEntryDto(Long userId, String username, String lastEntryTime) {
        this.userId = userId;
        this.username = username;
        this.lastEntryTime = formatLastEntryTime(lastEntryTime);
        this.periodOfDay = determinePeriodOfDay(lastEntryTime);
    }

    private String formatLastEntryTime(String lastEntryTime) {
        LocalDateTime dateTime = LocalDateTime.parse(lastEntryTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
        return dateTime.format(formatter);
    }

    private String determinePeriodOfDay(String lastEntryTime) {
        LocalDateTime dateTime = LocalDateTime.parse(lastEntryTime);
        int hour = dateTime.getHour();
        if (hour >= 5 && hour < 12) {
            return "Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Evening";
        } else {
            return "Night";
        }
    }

}