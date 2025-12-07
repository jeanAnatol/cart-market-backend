package com.market.cart.entity.enginespec;

import com.market.cart.entity.fueltype.FuelType;
import org.springframework.stereotype.Component;

@Component
public class EngineSpecMapper {

    public EngineSpec toEngineSpec(EngineSpecInsertDTO eSpecInsDTO, FuelType fuelType) {

        EngineSpec engineSpec = new EngineSpec();

        engineSpec.setDisplacementCC(eSpecInsDTO.displacement());
        engineSpec.setHorsePower(eSpecInsDTO.horsePower());
        engineSpec.setFuelType(fuelType.getName());
        engineSpec.setGearBoxType(eSpecInsDTO.gearboxType());

        return engineSpec;
    }

    public EngineSpecReadOnlyDTO toReadOnlyDTO(EngineSpec engineSpec) {

        EngineSpecReadOnlyDTO engineSpecReadOnlyDTO = new EngineSpecReadOnlyDTO();

        engineSpecReadOnlyDTO.setDisplacement(engineSpec.getDisplacementCC());
        engineSpecReadOnlyDTO.setFuelType(engineSpec.getFuelType());
        engineSpecReadOnlyDTO.setGearboxType(engineSpec.getGearBoxType());
        engineSpecReadOnlyDTO.setHorsePower(engineSpec.getHorsePower());

        return engineSpecReadOnlyDTO;
    }

    public EngineSpec updateEngineSpec(EngineSpec entity, EngineSpecUpdateDTO updateDTO) {

        if (updateDTO.displacement() != null) entity.setDisplacementCC(updateDTO.displacement());
//        if (updateDTO.fuelType() != null) entity.setFuelType(updateDTO.fuelType());
        if (updateDTO.gearboxType() != null) entity.setGearBoxType(updateDTO.gearboxType());
        if (updateDTO.horsePower() != null) entity.setHorsePower(updateDTO.horsePower());

        return entity;
    }
}
