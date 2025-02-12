package be.kdg.service;

import be.kdg.domain.RfidCard;
import be.kdg.domain.User;
import be.kdg.viewmodels.RfidCardViewModel;

import java.util.List;
import java.util.Optional;

public interface RfidCardService {
    List<RfidCard> findAll();
    Optional<RfidCard> findById(Long id);
    RfidCard save(RfidCardViewModel viewModel, User user);
    void deleteById(Long id);

    List<RfidCard> findByUserId(Long userId);

    long getRfidCount();
    Optional<RfidCard> findByCredentials(String credentials);

    RfidCard save(RfidCard rfidCard);
    long getRfidCountByUser(Long userId);
}
