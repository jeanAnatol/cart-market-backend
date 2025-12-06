package com.market.cart.entity.role;

import com.market.cart.entity.role.capability.Capability;
import com.market.cart.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "roles_capabilities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id")
    )
    private Set<Capability> capabilities = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void addCapability(Capability capability) {
        if (capabilities.isEmpty()) capabilities = new HashSet<>();
        this.capabilities.add(capability);
        capability.getRoles().add(this);
    }

    public void removeCapability(Capability capability) {
        this.capabilities.remove(capability);
        capability.getRoles().remove(this);
    }

    public void addUser(User user) {
        if (users == null) users = new HashSet<>();
        users.add(user);
        user.setRole(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setRole(null);
    }
}
