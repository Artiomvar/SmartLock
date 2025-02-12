package be.kdg.service;

import be.kdg.domain.Nfc;
import be.kdg.viewmodels.NfcViewModel;
import be.kdg.viewmodels.RfidCardViewModel;


import java.util.List;
import java.util.Optional;

public interface NfcService {
    List<Nfc> findAll();
    Optional<Nfc> findById(Long id);
    // UNCOMMENT WHEN DONE WITH FRONTEND VALIDATION
    //Nfc save(RfidCardViewModel viewModel);

    //UNCOMMENT FOR FRONTEND VALIDATION
    Nfc save(NfcViewModel viewModel);

    void deleteById(Long id);

    long getNfcCount();
    Optional<Nfc> findByCredentials(String credentials);
    long getNfcCountByUser(Long userId);


    List<Nfc> findNfcsByUserId(Long userId);

    Nfc save(Nfc nfc);


}
