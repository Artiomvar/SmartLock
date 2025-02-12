package be.kdg.service;

import be.kdg.domain.Fingerprint;
import be.kdg.viewmodels.FingerprintViewModel;

import java.util.List;
import java.util.Optional;

public interface FingerprintService {
    List<Fingerprint> findAll();
    Optional<Fingerprint> findById(Long id);
   Fingerprint save(FingerprintViewModel viewModel);
    void deleteById(Long id);

    long getFingerprintCount();

    long getFingerprintCountByUser(Long userId);

    Optional<Fingerprint> findByCredentials(String credentials);

    List<Fingerprint> findFingerprintsByUserId(Long userId);

    Fingerprint save(Fingerprint fingerprint);
}
