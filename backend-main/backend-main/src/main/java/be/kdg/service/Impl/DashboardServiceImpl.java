package be.kdg.service.Impl;

import be.kdg.service.*;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {


    private final RfidCardService rfidCardService;
    private final UserService userService;
    private final SensorService sensorService;
    private final NfcService nfcService;
    private final FingerprintService fingerprintService;

    public DashboardServiceImpl(RfidCardService rfidCardService, UserService userService, SensorService sensorService, NfcService nfcService, FingerprintService fingerprintService) {
        this.rfidCardService = rfidCardService;
        this.userService = userService;
        this.sensorService = sensorService;
        this.nfcService = nfcService;
        this.fingerprintService = fingerprintService;
    }


    @Override
    public long getRfidCount() {
        return rfidCardService.getRfidCount();
    }

    @Override
    public long getUserCount() {
        return userService.getUserCount();
    }

    @Override
    public long getSensorCount() {
        return sensorService.getSensorCount();
    }

    @Override
    public long getNfcCount() {
        return nfcService.getNfcCount();
    }

    @Override
    public long getFingerprintCount() {
        return fingerprintService.getFingerprintCount();
    }

        @Override
        public long getRfidCountForUser(Long userId) {
            return rfidCardService.getRfidCountByUser(userId);
        }


        @Override
        public long getSensorCountForUser(Long userId) {
            return sensorService.getSensorCountByUser(userId);
        }

        @Override
        public long getNfcCountForUser(Long userId) {
            return nfcService.getNfcCountByUser(userId);
        }

    @Override
    public long getFingerprintCountForUser(Long userId) {
        return fingerprintService.getFingerprintCountByUser(userId);
    }



}
