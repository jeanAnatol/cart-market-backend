package com.market.cart.entity.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {


    
    public User toUser(UserInsertDTO userInsertDTO) {

        User user = new User();

        user.setUsername(userInsertDTO.username());
        user.setPassword(userInsertDTO.password());
        user.setEmail(userInsertDTO.email());
        return user;
    }

    public UserReadOnlyDTO toReadOnlyDTO (User user) {

        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO();

        userReadOnlyDTO.setId(user.getId());
        userReadOnlyDTO.setUsername(user.getUsername());
        userReadOnlyDTO.setEmail(user.getEmail());
        userReadOnlyDTO.setRoleId(user.getRole().getId());

        return userReadOnlyDTO;
    }

    public User updateUser (UserInsertDTO uInsDTO, User user) {

        if (!uInsDTO.username().isEmpty()) {
            user.setUsername(uInsDTO.username());
        }
        if (!uInsDTO.email().isEmpty()) {
            user.setEmail(uInsDTO.email());
        }
        if (!uInsDTO.password().isEmpty()) {
            user.setPassword(uInsDTO.password());
        }

        return user;
    }
}
