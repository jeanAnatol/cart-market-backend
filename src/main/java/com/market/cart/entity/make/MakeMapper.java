package com.market.cart.entity.make;

import com.market.cart.entity.model.Model;
import com.market.cart.entity.vehicletype.VehicleType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MakeMapper {

    public Make toMake(MakeInsertDTO dto) {

        Make make = new Make();
        make.setName(dto.name());
        make.setVehicleTypes(new HashSet<>());
        make.setModels(new HashSet<>());

        return make;
    }

    public MakeReadOnlyDTO toReadOnlyDTO(Make make) {
        Set<String> modelNames = make.getModels()
                .stream()
                .map(Model::getName)
                .collect(Collectors.toSet());

        Set<String> vehicleTypeNames = make.getVehicleTypes()
                .stream()
                .map(VehicleType::getName)
                .collect(Collectors.toSet());

        return new MakeReadOnlyDTO(
                modelNames,
                vehicleTypeNames
        );
    }
}
