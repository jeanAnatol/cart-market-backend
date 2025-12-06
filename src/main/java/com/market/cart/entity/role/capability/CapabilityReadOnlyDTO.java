package com.market.cart.entity.role.capability;

import com.market.cart.entity.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CapabilityReadOnlyDTO {

    private String name;

    private String description;

    private Set<Role> roles;
}
