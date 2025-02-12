package be.kdg.service;

public interface DashboardService {
    long getRfidCount();

    long getUserCount();

    long getSensorCount();

    long getNfcCount();

    long getFingerprintCount();

    long getRfidCountForUser(Long userId);

    long getSensorCountForUser(Long userId);

    long getNfcCountForUser(Long userId);

    long getFingerprintCountForUser(Long userId);
}
