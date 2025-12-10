package com.market.cart.entity.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    public Role toRole(RoleInsertDTO roleInsertDTO) {

        Role role = new Role();
        role.setName(roleInsertDTO.name().toUpperCase());

        return role;
    }

    public RoleReadOnlyDTO toReadOnlyDTO(Role role) {

        return new RoleReadOnlyDTO(role.getName(),
                role.getUsers(),
                role.getCapabilities());
    }
}
