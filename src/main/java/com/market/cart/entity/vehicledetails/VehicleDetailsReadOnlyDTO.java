package com.market.cart.entity.vehicledetails;

import com.market.cart.entity.enginespec.EngineSpec;
import com.market.cart.entity.vehicletype.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehicleDetailsReadOnlyDTO {

    private String vehicleType;

    private String make;

    private String model;

    private String manufactureYear;

    private Long mileage;

    private String color;

    private String state;

    private String vehicleDescriptionText;
}
