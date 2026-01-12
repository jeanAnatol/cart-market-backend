package com.market.cart.entity.advertisement;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.market.cart.entity.abstractentity.AbstractEntity;
import com.market.cart.entity.attachment.Attachment;
import com.market.cart.entity.contactinfo.ContactInfo;
import com.market.cart.entity.location.Location;
import com.market.cart.entity.user.User;
import com.market.cart.entity.vehicledetails.VehicleDetails;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an advertisement published by a user.
 *
 * <p>
 * This is the core domain entity of the marketplace and aggregates:
 * </p>
 *
 * <ul>
 *     <li>{@link User} – the owner of the advertisement</li>
 *     <li>{@link Location} – geographic location of the vehicle</li>
 *     <li>{@link VehicleDetails} – technical vehicle information</li>
 *     <li>{@link ContactInfo} – seller contact information</li>
 *     <li>{@link Attachment} – associated images or files</li>
 * </ul>
 *
 * <p>
 * The entity uses eager fetching for critical associations to avoid
 * {@link org.hibernate.LazyInitializationException} during REST serialization.
 * </p>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "advertisements")
public class Advertisement extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column
    private String adName;

    @Column(nullable = false)
    private Double price;

    /**
     * Geographic location of the advertisement.
     *
     * <p>
     * Uses {@link JsonManagedReference} to prevent infinite JSON recursion.
     * </p>
     */
    @OneToOne(mappedBy = "advertisement",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference
    private Location location;

    /**
     * Vehicle technical details.
     * (contains {@link com.market.cart.entity.enginespec.EngineSpec})
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vehicle_details_id")
    private VehicleDetails vehicleDetails;

    /**
     * Owner of the advertisement.
     *
     * <p>
     * Uses eager fetching to avoid authorization issues during serialization.
     * Circular references are prevented using {@link JsonBackReference}.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contactInfo_id")
    private ContactInfo contactInfo;

    /**
     * Attachments (images) associated with the advertisement.
     *
     * <p>
     * Eager fetching is required to prevent lazy loading exceptions
     * during REST response serialization.
     * </p>
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "advertisement_id")  // this tells Hibernate to insert advertisement_id into attachments
    private Set<Attachment> attachments = new HashSet<>();

    /// Spring gives a UUID to entity just before persistence
    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
