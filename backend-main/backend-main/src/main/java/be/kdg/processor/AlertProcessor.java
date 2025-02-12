package be.kdg.processor;

import be.kdg.domain.Notification;
import be.kdg.domain.User;
import be.kdg.service.NotificationService;
import be.kdg.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AlertProcessor implements DataProcessor {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // For JSON parsing
    private final NotificationService notificationService; // Injected NotificationService
    private final UserService userService; // Injected UserService
    private static final String FLASK_URL = "http://127.0.0.1:5000/predict"; // Flask endpoint

    public AlertProcessor(RestTemplate restTemplate, ObjectMapper objectMapper, NotificationService notificationService, UserService userService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public void process(String eventData) {
        try {
            // Parse the event data to extract the timestamp and user_id
            JsonNode eventJson = objectMapper.readTree(eventData);
            String timestamp = eventJson.get("timestamp").asText();
            int userId = eventJson.get("userId").asInt();

            // Convert timestamp to Hour, Minute, and Day_of_Week
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
            int hour = dateTime.getHour();
            int minute = dateTime.getMinute();
            int dayOfWeek = dateTime.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

            // Prepare the request payload for Flask
            Map<String, Object> flaskRequestData = new HashMap<>();
            flaskRequestData.put("Hour", hour);
            flaskRequestData.put("Minute", minute);
            flaskRequestData.put("Day_of_Week", dayOfWeek);
            flaskRequestData.put("User_ID", userId);

            // Send the POST request to Flask
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Object> entity = new HttpEntity<>(Collections.singletonList(flaskRequestData), headers);

            ResponseEntity<String> response = restTemplate.exchange(FLASK_URL, HttpMethod.POST, entity, String.class);

            // Check the Flask response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody().contains("unusual")) {
                System.out.println("AlertProcessor: Alert triggered for unusual data - " + eventData);

                // Fetch the User entity
                User user = userService.getUserById(userId);
                if (user == null) {
                    System.out.println("AlertProcessor: User not found for ID: " + userId);
                    return;
                }

                // Format the timestamp to a more user-friendly format
                DateTimeFormatter userFriendlyFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");
                String formattedTimestamp = dateTime.format(userFriendlyFormatter);

                // Create a detailed notification message
                String notificationMessage = String.format(
                        "Unusual activity was detected for %s on %s.",
                        user.getUsername(), // Assuming the `User` entity has a `getName()` method
                        formattedTimestamp
                );

                // Create and save the Notification
                Notification notification = new Notification(notificationMessage, user);
                notificationService.saveNotification(notification);

            } else {
                System.out.println("AlertProcessor: Data is usual for event - " + eventData);
            }

        } catch (Exception e) {
            System.out.println("AlertProcessor: Error processing event data - " + e.getMessage());
        }
    }
}
