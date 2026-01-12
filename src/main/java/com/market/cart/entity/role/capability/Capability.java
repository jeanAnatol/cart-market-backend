package com.market.cart.entity.role.capability;

import com.market.cart.entity.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a permission (capability) that can be assigned to Roles.
 *
 * <p>
 * Capabilities define what actions are allowed in the system and are grouped
 * through {@link Role} entities.
 * </p>
 *
 * <p>
 * Relationship:
 * <ul>
 *   <li>Many Capabilities ↔ Many Roles</li>
 * </ul>
 * </p>
 */
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

    /**
     * Roles that include this capability.
     *
     * <p>
     * At Role side maps to {@code Set<Capability>} capabilities;
     * This is the inverse side of the Role–Capability many-to-many relationship.
     * </p>
     */
    @ManyToMany(mappedBy = "capabilities", fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    /**
     * Assigns this Capability to a Role and maintains the bidirectional association.
     */
    public void addRole(Role role) {
        if (roles.isEmpty()) roles = new HashSet<>();
        this.roles.add(role);
        role.addCapability(this);
    }

    /**
     * Removes this Capability from a Role and maintains the bidirectional association.
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.removeCapability(this);
    }
}
