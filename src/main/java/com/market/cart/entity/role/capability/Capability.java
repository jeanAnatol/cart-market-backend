package com.market.cart.entity.role.capability;

import com.market.cart.entity.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "capabilities")
public class Capability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    //Στην πλευρά των Role κανει mapping στο σετ capabilities
    @ManyToMany(mappedBy = "capabilities", fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        if (roles.isEmpty()) roles = new HashSet<>();
        this.roles.add(role);
        role.addCapability(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.removeCapability(this);
    }
}
