package com.market.cart.entity.model;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.vehicletype.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public Model toModel(ModelInsertDTO insertDTO, Make make, VehicleType vehicleType) {
        Model model = new Model();
        model.setName(insertDTO.name());
        model.setMake(make);
        model.setVehicleType(vehicleType);

        return model;
    }

    public ModelReadOnlyDTO toReadOnlyDTO(Model model) {
        return new ModelReadOnlyDTO(
                model.getId(),
                model.getName(),
                model.getMake().getName(),
                model.getVehicleType().getName()
                );
    }
}
