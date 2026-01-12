package com.market.cart.entity.model;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.make.MakeRepository;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for managing {@link Model} entities.
 *
 * <p>
 * Handles creation, update, deletion, and reassignment of models
 * between {@link Make} and {@link VehicleType}.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final ModelRepository modelRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new Model and associates it with a Make and VehicleType.
     */
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

        log.debug("Model '{}' created under Make '{}' and VehicleType '{}'",
                model.getName(),
                make.getName(),
                vehicleType.getName());

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

    /**
     * Updates Model fields and optionally reassigns Make and VehicleType.
     */
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

            boolean exists = newMake.getModels().contains(model);
            if (oldMake != newMake && !exists) {
                log.debug("Reassigning Model '{}' from Make '{}' to '{}'",
                        model.getName(),
                        oldMake.getName(),
                        newMake.getName());

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

            if (!oldVehicleType.equals(newVehicleType)) {
                log.debug("Reassigning Model '{}' from VehicleType '{}' to '{}'",
                        model.getName(),
                        oldVehicleType.getName(),
                        newVehicleType.getName());

                oldVehicleType.getModels().remove(model);
                model.setVehicleType(newVehicleType);
                newVehicleType.getModels().add(model);
            }
        }
        log.info("Updating Model with id: {} and name: {}", updateDTO.modelId(), model.getName());

        return modelMapper.toReadOnlyDTO(modelRepository.save(model));
    }

    /**
     * Deletes a Model and clears all associations.
     */
    @Transactional
    public void deleteModel(Long modelId) {
        Model model = modelRepository.findById(modelId)
                        .orElseThrow(() ->
                                new CustomTargetNotFoundException("No Model found with id: "+ modelId, "modelService"));

        Make make = model.getMake();
        make.getModels().remove(model);

        VehicleType vehicleType = model.getVehicleType();
        vehicleType.getModels().remove(model);

        log.warn("Removed Model with id: {} and name: {}", modelId, model.getName());
        modelRepository.delete(model);
    }
}
