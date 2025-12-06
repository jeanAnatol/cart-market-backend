package com.market.cart.entity.vehicletype;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.make.MakeRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final VehicleTypeMapper vehicleTypeMapper;

    @Transactional
    public VehicleTypeReadOnlyDTO createVehicleType(VehicleTypeInsertDTO insertDTO) {

        if (insertDTO == null) {
            throw new CustomInvalidArgumentException("VehicleType name cannot be blank", "VehicleTypeService");
        }

        if (vehicleTypeRepository.findByName(insertDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Vehicle type already exists with name: "+ insertDTO.name(), "vehicleTypeService");
        }
        VehicleType vehicleType = vehicleTypeRepository.save(
                vehicleTypeMapper.toVehicleType(insertDTO)
        );

        return vehicleTypeMapper.toReadOnlyDTO(vehicleType);
    }

    public VehicleTypeReadOnlyDTO updateVehicleType(Long typeId, String name) {

        VehicleType vehicleType = vehicleTypeRepository.findById(typeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("No vehicle type found with ID: " + typeId, "vehicleTypeService"));
        vehicleType.setName(name);

        VehicleType saved = vehicleTypeRepository.save(vehicleType);
        return vehicleTypeMapper.toReadOnlyDTO(saved);
    }

    @Transactional(readOnly = true)
    public java.util.List<VehicleType> getAll() {
        return vehicleTypeRepository.findAll();
    }

    @Transactional
    public void deleteVehicleType(String name) {
        VehicleType vehicleType = vehicleTypeRepository.findByName(name)
                .orElseThrow(() -> new CustomTargetNotFoundException("VehicleType not found with name: " + name, "VehicleTypeService"));

        // remove from makes
        if (vehicleType.getMakes() != null) {
            for (Make make : new HashSet<>(vehicleType.getMakes())) {
                make.getVehicleTypes().remove(vehicleType);
                makeRepository.save(make);
            }
        }
        vehicleTypeRepository.delete(vehicleType);
    }
}
