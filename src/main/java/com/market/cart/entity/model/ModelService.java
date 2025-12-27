package com.market.cart.entity.model;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.make.MakeRepository;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository modelRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ModelReadOnlyDTO addModel(ModelInsertDTO insertDTO) {

        if(insertDTO.name().isBlank() || insertDTO.makeId() == null || insertDTO.vehicleTypeId() == null) {
            throw new CustomInvalidArgumentException("Insert values for new model cannot be blank or null", "modelService");
        }

        if (modelRepository.findByName(insertDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Model with name: "+insertDTO.name()+ " already exists.", "modelService");
        }

        Make make = makeRepository.findById(insertDTO.makeId())
                .orElseThrow(() -> new CustomTargetNotFoundException("No make found with id: "+insertDTO.makeId(), "modelService"));

        VehicleType vehicleType = vehicleTypeRepository.findById(insertDTO.vehicleTypeId())
                .orElseThrow(() -> new CustomTargetNotFoundException("No Vehicle Type found with id: "+insertDTO.vehicleTypeId(), "modelService"));

        Model model = modelMapper.toModel(insertDTO, make, vehicleType);
        make.getModels().add(model);
        vehicleType.getModels().add(model);

        return modelMapper.toReadOnlyDTO(modelRepository.save(model));
    }

    @Transactional(readOnly = true)
    public Set<ModelReadOnlyDTO> getAllModels() {
        return modelRepository.findAll()
                .stream().map(modelMapper::toReadOnlyDTO).collect(Collectors.toUnmodifiableSet());
    }

    @Transactional(readOnly = true)
    public Model getById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new CustomTargetNotFoundException("Model not found: " + id, "ModelService"));
    }

    @Transactional
    public ModelReadOnlyDTO updateModel(ModelUpdateDTO updateDTO) {

        Model model = modelRepository.findById(updateDTO.modelId())
                .orElseThrow(() ->
                        new CustomTargetNotFoundException("No model found with id: "+updateDTO.modelId(), "modelService"));

        model.setName(updateDTO.name());

        if (updateDTO.makeId() != null) {

            Make oldMake = model.getMake();

            Make newMake = makeRepository.findById(updateDTO.makeId())
                    .orElseThrow(() ->
                            new CustomTargetNotFoundException("No Make found with id: "+updateDTO.makeId(), "modelService"));
            if (oldMake != newMake) {
                oldMake.getModels().remove(model);
                model.setMake(newMake);
                newMake.getModels().add(model);
            }
        }
        if (updateDTO.vehicleTypeId() != null) {
            VehicleType oldVehicleType = model.getVehicleType();

            VehicleType newVehicleType = vehicleTypeRepository.findById(updateDTO.vehicleTypeId())
                    .orElseThrow(() ->
                            new CustomTargetNotFoundException("No Vehicle Type found with id: "+updateDTO.vehicleTypeId(), "modelService"));

            if (oldVehicleType != newVehicleType) {
                oldVehicleType.getModels().remove(model);
                model.setVehicleType(newVehicleType);
                newVehicleType.getModels().add(model);
            }
        }

        return modelMapper.toReadOnlyDTO(modelRepository.save(model));
    }

    @Transactional
    public void deleteModel(Long modelId) {
        Model model = modelRepository.findById(modelId)
                        .orElseThrow(() ->
                                new CustomTargetNotFoundException("No Model found with id: "+ modelId, "modelService"));

        Make make = model.getMake();
        make.getModels().remove(model);

        VehicleType vehicleType = model.getVehicleType();
        vehicleType.getModels().remove(model);

        modelRepository.delete(model);
    }
}
