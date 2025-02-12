package be.kdg.mapper;

import be.kdg.domain.User;
import be.kdg.viewmodels.UserViewModel;

import java.util.Objects;

public class UserMapper {

    public static User toEntity(UserViewModel viewModel) {
        if(Objects.isNull(viewModel)){
            return null;
        }
       return User.builder()
                .username(viewModel.getUsername())
                .password(viewModel.getPassword())
                .email(viewModel.getEmail())
                .build();
    }

    public static UserViewModel toViewModel(User user){
        if(Objects.isNull(user)){
            return null;
        }
       return UserViewModel.builder()
               .username(user.getUsername())
               .password(user.getPassword())
               .email(user.getEmail())
               .build();
    }
}
