package be.kdg;

import be.kdg.domain.*;
import be.kdg.repository.*;
import org.springframework.boot.CommandLineRunner;
import be.kdg.domain.Notification;
import be.kdg.repository.NotificationRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            SensorRepository sensorRepository,
            UserRepository userRepository,
            RfidCardRepository rfidCardRepository,
            NfcRepository nfcRepository,
            FingerprintRepository fingerprintRepository,
            LogRepository logRepository,
            NotificationRepository notificationRepository) {
        return args -> {
            // Seed sensors if the repository is empty
            if (sensorRepository.findAll().isEmpty()) {
                Sensor doorSensor = new Sensor();
                doorSensor.setName("Main Door Lock");
                doorSensor.setMac("34:B7:DA:65:F4:90");
                doorSensor.setIpAddress("172.20.10.7");
                doorSensor.setStatus(true);
                doorSensor.setRfid(true);
                doorSensor.setNfc(true); // Enable NFC support
                doorSensor.setFingerprint(false);
                sensorRepository.save(doorSensor);

                Sensor secondarySensor = new Sensor();
                secondarySensor.setName("Secondary Lock");
                secondarySensor.setMac("00:1B:44:11:3A:B7");
                secondarySensor.setIpAddress("192.168.1.5");
                secondarySensor.setStatus(true);
                secondarySensor.setRfid(false);
                secondarySensor.setNfc(true);
                secondarySensor.setFingerprint(false);
                sensorRepository.save(secondarySensor);
            }

            // Seed users if the repository is empty
            if (userRepository.findAll().isEmpty()) {
                Sensor doorSensor = sensorRepository.findByName("Main Door Lock");

                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword("password123");
                adminUser.setEmail("admin@example.com");
                adminUser.setActive(true);
                adminUser.setAccessibleSensors(Set.of(doorSensor));
                userRepository.save(adminUser);

                User guestUser = new User();
                guestUser.setUsername("guest");
                guestUser.setPassword("guestpass");
                guestUser.setEmail("guest@example.com");
                guestUser.setActive(true);
                guestUser.setAccessibleSensors(Set.of(doorSensor));
                guestUser.setTemporaryAccessStart(LocalDateTime.now());
                guestUser.setTemporaryAccessEnd(LocalDateTime.now().plusHours(2));
                userRepository.save(guestUser);

                // Add RFID cards for users
                addRfidCard(rfidCardRepository, adminUser, "530017E456F6", true);
                addRfidCard(rfidCardRepository, guestUser, "guest_rfid_001", true);

                // Add NFC cards for users
                addNfcCard(nfcRepository, adminUser, "admin_nfc_001", true);
                addNfcCard(nfcRepository, guestUser, "guest_nfc_001", true);

                // Add Fingerprint cards for users
                addFingerprint(fingerprintRepository, adminUser, "admin_fingerprint_001", true);
                addFingerprint(fingerprintRepository, guestUser, "guest_fingerprint_001", true);
            }

            // Seed logs if the repository is empty
            if (logRepository.findAll().isEmpty()) {
                seedLogs(logRepository, userRepository, sensorRepository);
            }
            // Seed notifications if the repository is empty
            if (notificationRepository.findAll().isEmpty()) {
                List<User> users = userRepository.findAll();
                users.forEach(user -> {
                    addNotification(notificationRepository, user, "Welcome " + user.getUsername() + "! Your account has been created successfully.");
                    addNotification(notificationRepository, user, "Your NFC access has been enabled.");
                    if (user.getTemporaryAccessEnd() != null) {
                        addNotification(notificationRepository, user, "Temporary access has been activated.");
                        addNotification(notificationRepository, user, "Your temporary access will expire soon.");
                    }
                });
            }
        };
    }
    
    

    // Helper method to seed logs
    private void seedLogs(LogRepository logRepository, UserRepository userRepository, SensorRepository sensorRepository) {
        List<User> users = userRepository.findAll();
        List<Sensor> sensors = sensorRepository.findAll();

        if (!users.isEmpty() && !sensors.isEmpty()) {
            User admin = users.stream().filter(u -> u.getUsername().equals("admin")).findFirst().orElse(null);
            User guest = users.stream().filter(u -> u.getUsername().equals("guest")).findFirst().orElse(null);
            Sensor mainDoorLock = sensors.stream().filter(s -> s.getName().equals("Main Door Lock")).findFirst().orElse(null);

            if (admin != null && guest != null && mainDoorLock != null) {
                Log log1 = new Log(admin.getId(), (long) mainDoorLock.getId(), "RFID", true, LocalDateTime.now().minusDays(1));
                Log log2 = new Log(guest.getId(), (long) mainDoorLock.getId(), "FINGER_PRINT", false, LocalDateTime.now().minusHours(5));
                Log log3 = new Log(admin.getId(), (long) mainDoorLock.getId(), "NFC", true, LocalDateTime.now().minusMinutes(30));
                Log log4 = new Log(admin.getId(), (long) mainDoorLock.getId(), "RFID", true, LocalDateTime.now());

                logRepository.saveAll(List.of(log1, log2, log3, log4));
            }
        }
    }

    // Helper method to add RFID cards
    private void addRfidCard(RfidCardRepository rfidCardRepository, User user, String credentials, boolean isActive) {
        RfidCard rfidCard = new RfidCard();
        rfidCard.setName(user.getUsername() + "'s RFID Card");
        rfidCard.setCredentials(credentials);
        rfidCard.setActive(isActive);
        rfidCard.setUser(user);

        // Save the RFID card
        rfidCardRepository.save(rfidCard);
    }

    // Helper method to add NFC cards
    private void addNfcCard(NfcRepository nfcRepository, User user, String credentials, boolean isActive) {
        Nfc nfcCard = new Nfc();
        nfcCard.setName(user.getUsername() + "'s NFC Card");
        nfcCard.setCredentials(credentials);
        nfcCard.setActive(isActive);
        nfcCard.setUser(user);

        // Save the NFC card
        nfcRepository.save(nfcCard);
    }

    private void addFingerprint(FingerprintRepository fingerprintRepository, User user, String credentials, boolean isActive) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setName(user.getUsername() + "'s Fingerprint");
        fingerprint.setCredentials(credentials);
        fingerprint.setActive(isActive);
        fingerprint.setUser(user);

        // Save the fingerprint
        fingerprintRepository.save(fingerprint);
    }
    private void addNotification(NotificationRepository notificationRepository, User user, String message) {
        Notification notification = new Notification(message, user);
        notificationRepository.save(notification);
    }
}