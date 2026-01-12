package com.market.cart.entity.location;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.market.cart.entity.abstractentity.AbstractEntity;
import com.market.cart.entity.advertisement.Advertisement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

/**
 * <p>The Location class stores all location and spatial information of the advertisement:
 *  latitude and longitude in String.</p>
 *  Also stores coordinates in {@link Geometry}.
 *  The Location is created with every new Advertisement and wiped with every Advertisement deletion.
 */

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location_coordinates")
public class Location extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locationName;

    private String postalCode;

    /**
     * 1<->1 To advertisements - Location is a subEntity and relative to advertisement
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    @JsonBackReference /// prevents circular reference -> infinite loop
    private Advertisement advertisement;

    /**
     * Spatial column, ignored in json response
     */
    @JsonIgnore
    @Column(nullable = false)
    private Geometry coordinates;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;
}