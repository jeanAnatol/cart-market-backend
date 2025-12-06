package com.market.cart.entity.location;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.market.cart.entity.advertisement.Advertisement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location_coordinates")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locationName;

    private String postalCode;

///// 1<->1 To advertisements - Location is a subEntity and relative to advertisement, not a solid permanent entity
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    private Advertisement advertisement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id")
    @JsonBackReference // prevents infinite loop
    private Advertisement advertisement;

    // Spatial column
    @JsonIgnore
    @Column(
//            columnDefinition = "geometry(Point, 4326)",
            nullable = false)
    private Geometry coordinates;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;


}
