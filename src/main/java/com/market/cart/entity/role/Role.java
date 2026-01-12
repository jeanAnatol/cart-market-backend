package com.market.cart.entity.role;

import com.market.cart.entity.role.capability.Capability;
import com.market.cart.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an authorization Role within the system.
 *
 * <p>
 * A Role groups together a set of {@link Capability} permissions and can be
 * assigned to multiple {@link User} entities.
 * </p>
 *
 * <p>
 * Relationships:
 * <ul>
 *   <li>One Role → Many Users</li>
 *   <li>Many Roles ↔ Many Capabilities</li>
 * </ul>
 * </p>
 */
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

    /**
     * Users assigned to this role.
     */
    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    /**
     * Capabilities granted to this role.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "roles_capabilities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "capability_id")
    )
    private Set<Capability> capabilities = new HashSet<>();

    /**
     * Creates a Role with the given name.
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * <p>Assigns a Capability to this Role and updates the bidirectional association.</p>
     */
    public void addCapability(Capability capability) {
        if (capabilities.isEmpty()) capabilities = new HashSet<>();
        this.capabilities.add(capability);
        capability.getRoles().add(this);
    }

    /**
     * Removes a Capability from this Role and updates the bidirectional association.
     */
    public void removeCapability(Capability capability) {
        this.capabilities.remove(capability);
        capability.getRoles().remove(this);
    }

    /// Assigns a User to the Role maintaining both sides
    public void addUser(User user) {
        if (users == null) users = new HashSet<>();
        users.add(user);
        user.setRole(this);
    }

    /// Removes a User from Role maintaining both sides
    public void removeUser(User user) {
        this.users.remove(user);
        user.setRole(null);
    }
}
