package be.kdg.mapper;

import be.kdg.domain.Fingerprint;
import be.kdg.domain.User;
import be.kdg.viewmodels.FingerprintViewModel;

import java.util.Objects;

public class FingerprintMapper {
    public static Fingerprint toEntity(FingerprintViewModel fingerprintViewModel, User user) {
        if(Objects.isNull(fingerprintViewModel)) {
            return null;
        }

        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setId(fingerprintViewModel.getId());
        fingerprint.setName(fingerprintViewModel.getName());
        fingerprint.setCredentials(fingerprintViewModel.getCredential());
        fingerprint.setActive(fingerprintViewModel.isActive());
        fingerprint.setUser(user);
        return fingerprint;



    }

    public static FingerprintViewModel toViewModel(Fingerprint fingerprint) {
        if(Objects.isNull(fingerprint)) {
            return null;
        }

        FingerprintViewModel fingerprintViewModel = new FingerprintViewModel();
        fingerprintViewModel.setId(fingerprint.getId());
        fingerprintViewModel.setName(fingerprint.getName());
        fingerprintViewModel.setCredential(fingerprint.getCredentials());
        fingerprintViewModel.setActive(fingerprint.isActive());
        fingerprintViewModel.setUserId(String.valueOf(fingerprint.getUser().getId()));
        return fingerprintViewModel;


    }
}
