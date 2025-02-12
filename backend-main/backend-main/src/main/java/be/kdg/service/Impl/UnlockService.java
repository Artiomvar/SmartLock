package be.kdg.service.Impl;


import be.kdg.domain.Sensor;
import be.kdg.domain.UnlockRequest;
import be.kdg.domain.User;
import be.kdg.processor.CompositeProcessor;
import be.kdg.service.SensorService;
import be.kdg.service.UserService;
import be.kdg.strategy.UnlockStrategy;
import be.kdg.strategy.UnlockStrategyFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class UnlockService {

    private final UnlockStrategyFactory strategyFactory;
    private final CompositeProcessor compositeProcessor;
    private final SensorService sensorService; // Renamed from lockService for clarity
    private final RestTemplate restTemplate;

    public UnlockService(UnlockStrategyFactory strategyFactory,
                         CompositeProcessor compositeProcessor,
                         SensorService sensorService,
                         RestTemplate restTemplate) {
        this.strategyFactory = strategyFactory;
        this.compositeProcessor = compositeProcessor;
        this.sensorService = sensorService;
        this.restTemplate = restTemplate;
    }

    public void handleUnlock(UnlockRequest request) {
        // Validate that the request contains necessary data
        if (request.getMac() == null || request.getMac().isEmpty()) {
            throw new IllegalArgumentException("MAC address is required for unlocking.");
        }

        if (request.getIpAddress() == null || request.getIpAddress().isEmpty()) {
            throw new IllegalArgumentException("IP address is required for unlocking.");
        }

        // Fetch the Sensor using the MAC address
        Sensor sensor = sensorService.getSensorByMac(request.getMac());
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor not found for MAC address: " + request.getMac());
        }

        User user = new User(); // Default user object
        user.setId(0L); // Default user ID (modify if needed)

        boolean success = false;

        if ("web".equalsIgnoreCase(request.getMethod())) {
            // Use the IP address directly from the request for unlocking
            try {
                sendUnlockRequestToArduino(request.getIpAddress());
                success = true;
            } catch (Exception e) {
                success = false;
            }
        } else {
            // Unlock using the strategy
            UnlockStrategy unlockStrategy = strategyFactory.getStrategy(request.getMethod());
            success = unlockStrategy.validate((long) sensor.getId(), request.getCredentials());

            // Always fetch the user even if validation fails
            try {
                user = unlockStrategy.getValidatedUser();
            } catch (IllegalStateException e) {
                System.out.println("No user associated with the unlock attempt: " + e.getMessage());
            }
        }

        String eventData = prepareEventData(user.getId(), request, success);
        compositeProcessor.process(eventData); // Log the event regardless of success or failure

        if (!success) {
            throw new SecurityException("Unlock attempt failed for method: " + request.getMethod());
        }
    }
    private void sendUnlockRequestToArduino(String ipAddress) {
        String arduinoEndpoint = String.format("http://%s/unlock", ipAddress);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String payload = "{\"unlock\": true}";

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(arduinoEndpoint, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Unlock command sent successfully to Arduino.");
            } else {
                System.err.println("Failed to send unlock command to Arduino: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("Error while sending unlock command to Arduino: " + e.getMessage());
        }
    }

    public String sendRegisterRequestToArduino(String method, String arduinoIP) {
        String arduinoEndpoint = String.format("http://%s/register", arduinoIP);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String payload = String.format("{\"method\": \"%s\"}", method);
        System.out.println("Payload being sent: " + payload); // Debugging log

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(arduinoEndpoint, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Register command sent successfully to Arduino.");
                System.out.println("CREDENTIALS");
                System.out.println(response.getBody());
                return response.getBody(); // Return the credentials from Arduino
            } else {
                System.err.println("Failed to send register command to Arduino: " + response.getBody());
                throw new RuntimeException("Arduino responded with error: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error while sending register command to Arduino: " + e.getMessage());
            throw new RuntimeException("Failed to send request to Arduino: " + e.getMessage());
        }
    }



    private String prepareEventData(Long userId, UnlockRequest request, boolean success) {
        return String.format(
                "{\"userId\": %s, \"lockId\": %s, \"mac\": \"%s\", \"method\": \"%s\", \"success\": %b, \"timestamp\": \"%s\"}",
                userId,
                sensorService.getSensorByMac(request.getMac()).getId(),
                request.getMac(),
                request.getMethod(),
                success,
                LocalDateTime.now()
        );
    }
}
