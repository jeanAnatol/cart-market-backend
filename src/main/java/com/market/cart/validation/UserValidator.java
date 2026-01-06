package com.market.cart.validation;

import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserInsertDTO;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.entity.user.UserUpdateDTO;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void validateInsertDTO(UserInsertDTO uInsDTO) {

        if(uInsDTO == null) {
            throw new CustomInvalidArgumentException("UserInsertDTO cannot be null.", "userValidator");
        }
        if (uInsDTO.username().isEmpty() ||
        uInsDTO.email().isEmpty() ||
        uInsDTO.password().isEmpty()) {
            throw new CustomInvalidArgumentException("\n\nOne of the following values are empty or blank which is forbidden: \n" +uInsDTO.username()+"\n"+uInsDTO.email()+"\n or the password.", "userValidator-validateInsertDTO");
        }
    }

    public void validateUpdateDTO(UserUpdateDTO updateDTO, User user) {

        if (updateDTO.newPassword() != null) {
            if (!passwordEncoder.matches(updateDTO.currentPassword(), user.getPassword())) {
                throw new BadCredentialsException("Wrong password");
            }
            user.setPassword(passwordEncoder.encode(updateDTO.newPassword()));
        }
    }
}
