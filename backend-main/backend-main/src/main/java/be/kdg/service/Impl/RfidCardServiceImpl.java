package be.kdg.service.Impl;

import be.kdg.domain.RfidCard;
import be.kdg.domain.User;
import be.kdg.mapper.RfidCardMapper;
import be.kdg.repository.RfidCardRepository;
import be.kdg.service.RfidCardService;
import be.kdg.service.UserService;
import be.kdg.viewmodels.RfidCardViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RfidCardServiceImpl implements RfidCardService {

    private final RfidCardRepository rfidCardRepository;
    private final UserService userService;

    @Autowired
    public RfidCardServiceImpl(RfidCardRepository rfidCardRepository, UserService userService) {
        this.rfidCardRepository = rfidCardRepository;
        this.userService = userService;
    }

    @Override
    public List<RfidCard>   findByUserId(Long userId) {
        return rfidCardRepository.findByUserId(userId);
    }

    @Override
    public long getRfidCount() {
        return rfidCardRepository.count();
    }

    @Override
    public Optional<RfidCard> findByCredentials(String credentials) {
        return rfidCardRepository.findByCredentials(credentials);
    }

    @Override
    public RfidCard save(RfidCard rfidCard) {
        return rfidCardRepository.save(rfidCard);
    }

    @Override
    public long getRfidCountByUser(Long userId) {
        return rfidCardRepository.countByUserId(userId);
    }

    @Override
    public List<RfidCard> findAll() {
        return rfidCardRepository.findAll();
    }

    @Override
    public Optional<RfidCard> findById(Long id) {
        return rfidCardRepository.findById(id);
    }



    @Override
    public RfidCard save(RfidCardViewModel viewModel,User user) {
       
        RfidCard rfidCard = RfidCardMapper.toEntity(viewModel, user);
        if (Objects.nonNull(rfidCard)) {
            return rfidCardRepository.save(rfidCard);
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        rfidCardRepository.deleteById(id);
    }
}
