package com.market.cart.entity.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    
    public User toUser(UserInsertDTO userInsertDTO) {

        User user = new User();

        user.setUsername(userInsertDTO.username());
        user.setPassword(passwordEncoder.encode(userInsertDTO.password()));
        user.setEmail(userInsertDTO.email());
        return user;
    }

    public UserReadOnlyDTO toReadOnlyDTO (User user) {

        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO();

        userReadOnlyDTO.setUsername(user.getUsername());
        userReadOnlyDTO.setEmail(user.getEmail());
        userReadOnlyDTO.setRole(user.getRole().getName());

        return userReadOnlyDTO;
    }

    public User updateUser (UserUpdateDTO updateDTO, User user) {

        if (!updateDTO.username().isEmpty()) {
            user.setUsername(updateDTO.username());
        }
        if (!updateDTO.email().isEmpty()) {
            user.setEmail(updateDTO.email());
        }
        if (!updateDTO.newPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDTO.newPassword()));
        }

        return user;
    }
}
