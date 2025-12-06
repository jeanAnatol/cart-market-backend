package com.market.cart.entity.enginespec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EngineSpecReadOnlyDTO {

    private Integer displacement;

    private String fuelType;

    private String gearboxType;

    private Integer horsePower;

}
