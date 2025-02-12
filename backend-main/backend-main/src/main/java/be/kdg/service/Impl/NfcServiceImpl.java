package be.kdg.service.Impl;

import be.kdg.domain.Nfc;
import be.kdg.domain.User;
//import be.kdg.mapper.NfcMapper; // Assuming you have a mapper for NFC entities
import be.kdg.mapper.NfcMapper;
import be.kdg.repository.NfcRepository;
import be.kdg.service.NfcService;
import be.kdg.service.UserService;
//import be.kdg.viewmodels.NfcViewModel; // Assuming you have a ViewModel for NFC
import be.kdg.viewmodels.NfcViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class NfcServiceImpl implements NfcService {



    private final NfcRepository nfcRepository;
    private final UserService userService;
    // ADDITIONAL COMMENT: Repository supports counting NFCs by a specific user.

    @Autowired
    public NfcServiceImpl(NfcRepository nfcRepository, UserService userService) {
        this.nfcRepository = nfcRepository;
        this.userService = userService;
    }

    @Override
    public long getNfcCount() {
        return nfcRepository.count();
    }

    @Override
    public Optional<Nfc> findByCredentials(String credentials) {
        return nfcRepository.findByCredentials(credentials);
    }

    @Override
    public long getNfcCountByUser(Long userId) {
       return nfcRepository.countByUserId(userId);
    }
    @Override
    public List<Nfc> findNfcsByUserId(Long userId) {
        return nfcRepository.findByUserId(userId);
    }


    @Override
    public Nfc save(Nfc nfc) {
        return nfcRepository.save(nfc);
    }


    @Override
    public List<Nfc> findAll() {
        return nfcRepository.findAll();
    }

    @Override
    public Optional<Nfc> findById(Long id) {
        return nfcRepository.findById(id);
    }

    //UNCOMMENT FOR FRONTEND VALIDATION
    @Override
    public Nfc save(NfcViewModel viewModel) {
       User user = userService.getUserById(Integer.parseInt(viewModel.getUserID()));
        Nfc nfc = NfcMapper.toEntity(viewModel, user);
        if (Objects.nonNull(nfc)) {
            return nfcRepository.save(nfc);
       }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        nfcRepository.deleteById(id);
    }


}
