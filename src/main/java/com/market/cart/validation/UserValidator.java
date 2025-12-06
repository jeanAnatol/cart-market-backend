package com.market.cart.validation;

import com.market.cart.entity.user.UserInsertDTO;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateInsertDTO(UserInsertDTO uInsDTO) {

        if(uInsDTO == null) {
            throw new CustomInvalidArgumentException("UserInsertDTO cannot be null.", "userValidator");
        }
    }
}
