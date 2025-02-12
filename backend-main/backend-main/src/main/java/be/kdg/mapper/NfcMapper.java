package be.kdg.mapper;

import be.kdg.domain.Nfc;
import be.kdg.domain.User;
import be.kdg.viewmodels.NfcViewModel;

import java.util.Objects;

public class NfcMapper {
    public static Nfc toEntity(NfcViewModel nfcViewModel, User user) {
        if(Objects.isNull(nfcViewModel)) {
            return null;
        }

        Nfc nfc = new Nfc();
        nfc.setId(nfcViewModel.getId());
        nfc.setName(nfcViewModel.getName());
        nfc.setCredentials(nfcViewModel.getCredentials());
        nfc.setActive(nfcViewModel.isActive());
        nfc.setUser(user);
        return nfc;
    }

    public static NfcViewModel toViewModel(Nfc nfc) {
        if(Objects.isNull(nfc)) {
            return null;
        }
        NfcViewModel nfcViewModel = new NfcViewModel();
        nfcViewModel.setId(nfc.getId());
        nfcViewModel.setName(nfc.getName());
        nfcViewModel.setCredentials(nfc.getCredentials());
        nfcViewModel.setActive(nfc.isActive());
        nfcViewModel.setUserID(String.valueOf(nfc.getUser().getId()));
        return nfcViewModel;
    }
}
