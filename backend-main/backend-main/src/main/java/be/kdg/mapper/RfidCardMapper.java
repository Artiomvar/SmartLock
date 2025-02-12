package be.kdg.mapper;

import be.kdg.domain.RfidCard;
import be.kdg.domain.User;
import be.kdg.viewmodels.RfidCardViewModel;

import java.util.Objects;

public class RfidCardMapper {

    public static RfidCard toEntity(RfidCardViewModel viewModel, User user) {
        if (Objects.isNull(viewModel)) {
            return null;
        }

        RfidCard rfidCard = new RfidCard();
        rfidCard.setId(viewModel.getId());
        rfidCard.setName(viewModel.getName());
        rfidCard.setCredentials(viewModel.getCredentials());
        rfidCard.setActive(viewModel.isActive());
        rfidCard.setUser(user);

        return rfidCard;
    }

    public static RfidCardViewModel toViewModel(RfidCard rfidCard) {
        if (Objects.isNull(rfidCard)) {
            return null;
        }

        RfidCardViewModel viewModel = new RfidCardViewModel();
        viewModel.setId(rfidCard.getId());
        viewModel.setName(rfidCard.getName());
        viewModel.setCredentials(rfidCard.getCredentials());
        viewModel.setActive(rfidCard.isActive());
        viewModel.setUserId(rfidCard.getUser().getId());

        return viewModel;
    }
}
