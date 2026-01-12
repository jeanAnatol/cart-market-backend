package com.market.cart.entity.user;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.cart.entity.advertisement.Advertisement;
import com.market.cart.entity.abstractentity.AbstractEntity;
import com.market.cart.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an application user and acts as the security principal.
 *
 * <p>
 * Implements {@link UserDetails} to integrate with Spring Security.
 * Users are associated with a single {@link Role}, which determines
 * both role-based and capability-based authorities.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AbstractEntity implements UserDetails {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Assigned role of the user.
     *
     * <p>
     * Determines the user's authorities and access scope.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * Advertisements created by the user.
     *
     * <p>
     * Managed bidirectionally with orphan removal enabled.
     * </p>
     */
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Advertisement> advertisements = new HashSet<>();

    /**
     * Constructor excluding ID and UUID.
     */
    public User (String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    /**
     * Assigns a role to the user and maintains bidirectional relationship.
     */
    public void addRole(Role role) {
        this.role = role;
        role.addUser(this);
    }

    /**
     * <h2>Currently not in use.</h2>
     * No infrastructure to support a user without a role in any event that this might happen.
     * Removes the role from the user and clears the association.
     */
//    public void removeRole(Role role) {
//        this.role = null;
//        role.removeUser(this);
//    }

    @PrePersist
    public void generateUuid() {
    if (uuid == null) {
        uuid = UUID.randomUUID().toString();
    }
}

    /**
     * Returns all granted authorities for the user.
     *
     * <p>
     * Includes:
     * <ul>
     *   <li>The user's role with {@code ROLE_} prefix</li>
     *   <li>All capabilities assigned to the role</li>
     * </ul>
     * </p>
     *
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        role.getCapabilities().forEach(capability -> grantedAuthorities.add(new SimpleGrantedAuthority(capability.getName())));
        return grantedAuthorities;
    }
}
