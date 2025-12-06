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

//    ///  1<->1 Each location plays the role of a timestamp for every advertisement
//    ///     regardless if they represent the same true geographical location on map.
//    @OneToOne(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "location_coordinates_id")
//    private Location location;

    @OneToOne(mappedBy = "advertisement",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonManagedReference // safe serialization
    private Location location;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vehicle_details_id")
    private VehicleDetails vehicleDetails;


    ///  When this was set to LAZY, application denied GET requests with the following prompt:
    ///  Access denied. Message: Full authentication is required to access this resource
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contactInfo_id")
    private ContactInfo contactInfo;


    /// One advertisementId can have many attachments(photos)

    /// When set to LAZY throws:
    /// Request processing failed: org.hibernate.LazyInitializationException:
    /// failed to lazily initialize a collection of role: com.market.cart.entity.advertisement.Advertisement.attachments:
    /// could not initialize proxy - no Session] with root cause
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "advertisement_id")  // this tells Hibernate to insert advertisement_id into attachments
    private Set<Attachment> attachments = new HashSet<>();

    @PrePersist
    public void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

}
