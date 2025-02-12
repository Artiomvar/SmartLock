package be.kdg.controller;

import be.kdg.dto.UserLastEntryDto;
import be.kdg.dto.UserLastEntryOwnDto;
import be.kdg.service.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
@Controller
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/users/last-entry-times")
    public List<UserLastEntryDto> getLastEntryTimes() {
        return logService.getLastEntryTimes();
    }

    @GetMapping("/user/{userId}/last-entry")
    public List<UserLastEntryOwnDto> getLastLogEntryByUserId(@PathVariable Long userId) {
        return logService.getLastEntryByUserId(userId);
    }
}
