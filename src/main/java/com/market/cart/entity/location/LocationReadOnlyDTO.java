package com.market.cart.entity.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.market.cart.entity.advertisement.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Geometry;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationReadOnlyDTO {

    private String locationName;

    private String longitude;

    private String latitude;

    private String postalCode;

    @JsonIgnore
    private Geometry geometry;
}
