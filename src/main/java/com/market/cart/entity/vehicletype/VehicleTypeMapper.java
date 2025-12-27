package com.market.cart.entity.vehicletype;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.make.MakeReadOnlyDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleTypeMapper {

    public VehicleType toVehicleType(VehicleTypeInsertDTO dto) {
        VehicleType vehicleType = new VehicleType();
        vehicleType.setName(dto.name());
        return vehicleType;
    }

    public VehicleTypeReadOnlyDTO toReadOnlyDTO(VehicleType vehicleType, Set<MakeReadOnlyDTO> makes) {
//        Set<String> makeNames = vehicleType.getMakes().stream()
//                .map(Make::getName)
//                .collect(Collectors.toSet());

        return new VehicleTypeReadOnlyDTO(vehicleType.getId(), vehicleType.getName(), makes);
    }
}
