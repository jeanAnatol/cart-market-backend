package com.market.cart.entity.vehicledetails;

import com.market.cart.entity.enginespec.EngineSpec;
import com.market.cart.entity.make.Make;
import com.market.cart.entity.model.Model;
import com.market.cart.entity.vehicletype.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class VehicleDetailsMapper {

    public VehicleDetails toVehicleDetails(VehicleDetailsInsertDTO vDetailsInsDTO, EngineSpec engineSpec, VehicleType vehicleType, Make make, Model model) {

        VehicleDetails vehicleDetails = new VehicleDetails();
        vehicleDetails.setVehicleType(vehicleType.getName());
        vehicleDetails.setMake(make.getName());
        vehicleDetails.setModel(model.getName());
        vehicleDetails.setEngineSpec(engineSpec);
        vehicleDetails.setManufactureYear(vDetailsInsDTO.manufactureYear());
        vehicleDetails.setMileage(vDetailsInsDTO.mileage());
        vehicleDetails.setColor(vDetailsInsDTO.color());
        vehicleDetails.setState(vDetailsInsDTO.state());
        vehicleDetails.setVehicleDescriptionText(vDetailsInsDTO.vehicleDescriptionText());

        return vehicleDetails;
    }

    public VehicleDetailsReadOnlyDTO toReadOnlyDTO(VehicleDetails vehicleDetails) {

        VehicleDetailsReadOnlyDTO vehicleDetailsReadOnlyDTO = new VehicleDetailsReadOnlyDTO();
        vehicleDetailsReadOnlyDTO.setVehicleType(vehicleDetails.getVehicleType());
        vehicleDetailsReadOnlyDTO.setMake(vehicleDetails.getMake());
        vehicleDetailsReadOnlyDTO.setModel(vehicleDetails.getModel());
        vehicleDetailsReadOnlyDTO.setManufactureYear(vehicleDetails.getManufactureYear());
        vehicleDetailsReadOnlyDTO.setMileage(vehicleDetails.getMileage());
        vehicleDetailsReadOnlyDTO.setColor(vehicleDetails.getColor());
        vehicleDetailsReadOnlyDTO.setState(vehicleDetails.getState());
        vehicleDetailsReadOnlyDTO.setVehicleDescriptionText(vehicleDetails.getVehicleDescriptionText());

        return vehicleDetailsReadOnlyDTO;
    }

    public VehicleDetailsReadOnlyDTO updateVehicleDetails(VehicleDetails entity, VehicleDetailsUpdateDTO updateDTO) {

//        if (updateDTO.vehicleType() != null) entity.setVehicleType(updateDTO.vehicleType());
//        if (updateDTO.make() != null) entity.setMake(updateDTO.make());
//        if (updateDTO.model() != null) entity.setModel(updateDTO.model());
        if (updateDTO.manufactureYear() != null) entity.setManufactureYear(updateDTO.manufactureYear());
        if (updateDTO.mileage() != null) entity.setMileage(updateDTO.mileage());
        if (updateDTO.color() != null) entity.setColor(updateDTO.color());
        if (updateDTO.state() != null) entity.setState(updateDTO.state());
        if (updateDTO.vehicleDescriptionText() != null) entity.setVehicleDescriptionText(updateDTO.vehicleDescriptionText());

        return toReadOnlyDTO(entity);
    }
}
