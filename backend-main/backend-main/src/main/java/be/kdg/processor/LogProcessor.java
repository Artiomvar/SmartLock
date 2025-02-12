package be.kdg.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import be.kdg.domain.Log;
import be.kdg.service.LogService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LogProcessor implements DataProcessor {

    private final LogService logService;
    private final ObjectMapper objectMapper;

    public LogProcessor(LogService logService, ObjectMapper objectMapper) {
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(String eventData) {
        System.out.println("LogProcessor: Logging event data - " + eventData);

        try {
            // Parse the eventData JSON string using Jackson
            JsonNode json = objectMapper.readTree(eventData);

            Long userId = json.path("userId").asLong(0);
            Long lockId = json.path("lockId").asLong(0);
            String method = json.path("method").asText("unknown");
            boolean success = json.path("success").asBoolean(false);
            String timestampString = json.path("timestamp").asText(LocalDateTime.now().toString());
            LocalDateTime timestamp = LocalDateTime.parse(timestampString);

            // Create and save the Log object
            Log log = new Log(userId, lockId, method, success, timestamp);
            logService.saveLog(log);

        } catch (Exception e) {
            System.err.println("LogProcessor: Failed to process event data - " + e.getMessage());
        }
    }
}
