package com.market.cart.entity.user;

import com.market.cart.entity.advertisement.Advertisement;
import com.market.cart.entity.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.sql.ast.SqlTreeCreationException;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserReadOnlyDTO {

    private String username;

    private String email;

    private String role;

    private String uuid;

}
