package be.kdg.service.Impl;

import be.kdg.domain.Fingerprint;
import be.kdg.domain.User;
import be.kdg.mapper.FingerprintMapper;
import be.kdg.repository.FingerprintRepository;
import be.kdg.service.FingerprintService;
import be.kdg.service.UserService;
import be.kdg.viewmodels.FingerprintViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FingerprintServiceImpl implements FingerprintService {

    private final FingerprintRepository fingerprintRepository;
    private final UserService userService;

    @Autowired
    public FingerprintServiceImpl(FingerprintRepository fingerprintRepository, UserService userService) {
        this.fingerprintRepository = fingerprintRepository;
        this.userService = userService;
    }

    @Override
    public long getFingerprintCount() {
        return fingerprintRepository.count();
    }

    @Override
    public long getFingerprintCountByUser(Long userId) {
        return fingerprintRepository.countByUserId(userId);
    }

    @Override
    public Optional<Fingerprint> findByCredentials(String credentials) {
        return fingerprintRepository.findByCredentials(credentials);
    }

    @Override
    public List<Fingerprint> findFingerprintsByUserId(Long userId) {
        return fingerprintRepository.findByUserId(userId);
    }

    @Override
    public Fingerprint save(Fingerprint fingerprint) {
        return fingerprintRepository.save(fingerprint);
    }

    @Override
    public List<Fingerprint> findAll() {
        return fingerprintRepository.findAll();
    }

    @Override
    public Optional<Fingerprint> findById(Long id) {
        return fingerprintRepository.findById(id);
    }

    // FIX AFTER FINGERPRINT VEIWMODEL ADDED
//    @Override
    public Fingerprint save(FingerprintViewModel viewModel) {
       User user = userService.getUserById(Integer.parseInt(viewModel.getUserId()));
       Fingerprint fingerprint = FingerprintMapper.toEntity(viewModel, user);
        if (Objects.nonNull(fingerprint)) {
          return fingerprintRepository.save(fingerprint);
       }
      return null;
  }

    @Override
    public void deleteById(Long id) {
        fingerprintRepository.deleteById(id);
    }
    
    
}
