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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends AbstractEntity
        implements UserDetails  /// Implements the systemic Spring Principal
{

    @Id /// marks this as entity primary key - uniquely identifies an instance of this entity in the database
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /// Why use Long(wrapper class) instead of long(primitive): by default long will be 0, Long by default is null.
    /// Hibernate when tries to persist an entity understands null as a new entry to persist while 0 as an
    /// existing entity id in the table

    @Column(unique = true)
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Advertisement> advertisements = new HashSet<>();

    /// Constructor without id field
    public User (String username, String password, String email, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public void addRole(Role role) {
        this.role = role;
        role.addUser(this);
    }

    public void removeRole(Role role) {
        this.role = null;
        role.removeUser(this);
    }

    @PrePersist
    public void generateUuid() {
    if (uuid == null) {
        uuid = UUID.randomUUID().toString();
    }
}



    @Override   ///  from UserDetails implementation
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));     /// Add ROLE_ prefix - obligatory
        role.getCapabilities().forEach(capability -> grantedAuthorities.add(new SimpleGrantedAuthority(capability.getName())));
        return grantedAuthorities;
    }
}
