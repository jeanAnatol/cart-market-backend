package com.market.cart.entity.advertisement;


import com.market.cart.entity.contactinfo.ContactInfoReadOnlyDTO;
import com.market.cart.entity.enginespec.EngineSpecReadOnlyDTO;
import com.market.cart.entity.location.LocationReadOnlyDTO;
import com.market.cart.entity.vehicledetails.VehicleDetailsReadOnlyDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdvertisementReadOnlyDTO {

    private Long userId;

    private Double price;

    private VehicleDetailsReadOnlyDTO vehicleDetailsDTO;

    private EngineSpecReadOnlyDTO engineSpecDTO;

    private ContactInfoReadOnlyDTO contactInfoDTO;

    private LocationReadOnlyDTO locationDTO;

    private Set<String> imageUrl;
}

