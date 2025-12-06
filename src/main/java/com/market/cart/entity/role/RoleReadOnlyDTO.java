package com.market.cart.entity.role;

import com.market.cart.entity.role.capability.Capability;
import com.market.cart.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


public record RoleReadOnlyDTO(

    String name,
    Set<User> users,
    Set<Capability> capabilities
    ){
}
