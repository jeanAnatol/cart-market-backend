package com.market.cart.entity.make;

import com.market.cart.entity.model.Model;
import com.market.cart.entity.model.ModelRepository;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MakeService {

    private final MakeRepository makeRepository;
    private final ModelRepository modelRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeMapper makeMapper;

    @Transactional
    public MakeReadOnlyDTO createMake(MakeInsertDTO insertDTO) {

        if (insertDTO.name() == null || insertDTO.name().isBlank()) {
            throw new CustomInvalidArgumentException("Make name must not be blank", "MakeService");
        }

        if (makeRepository.findByName(insertDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Make with name: " +insertDTO.name()+ " already exists", "makeService");
        }

        VehicleType vehicleType = vehicleTypeRepository.findById(insertDTO.vehicleTypeId())
                .orElseThrow(() -> new CustomTargetNotFoundException("No Vehicle Type found with this id: "+ insertDTO.vehicleTypeId(), "makeService"));


        Make make = makeMapper.toMake(insertDTO);
        make.getVehicleTypes().add(vehicleType);
        vehicleType.getMakes().add(make);

        makeRepository.save(make);
        vehicleTypeRepository.save(vehicleType);

        return makeMapper.toReadOnlyDTO(make);
    }

    /// Make by ID
    @Transactional(readOnly = true)
    public Make getMakeById(Long id) {
        return makeRepository.findById(id)
                .orElseThrow(() -> new CustomTargetNotFoundException("Make not found with id: " + id, "MakeService"));
    }

    /// Make by name
    @Transactional(readOnly = true)
    public Make getMakeByName(String name) {
        return makeRepository.findByName(name)
                .orElseThrow(() -> new CustomTargetNotFoundException("Make not found with name: "+ name, "MakeService"));
    }

    /// All makes
    @Transactional(readOnly = true)
    public Set<MakeReadOnlyDTO> getAllMakes() {
        return makeRepository.findAll()
                .stream().map(makeMapper::toReadOnlyDTO).collect(Collectors.toUnmodifiableSet());
    }

    /// Update make
    @Transactional
    public MakeReadOnlyDTO updateMake(Long makeId, String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank or null");
        }
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Make not found with id: "+ makeId, "makeService"));

        make.setName(name);
        return makeMapper.toReadOnlyDTO(makeRepository.save(make));
    }

    /// Delete Make
    @Transactional
    public void deleteMake(Long makeId) {
        Make make = makeRepository.findById(makeId)
                        .orElseThrow(() -> new CustomTargetNotFoundException("No make found with ID: "+makeId, "makeService"));

        if (!make.getVehicleTypes().isEmpty()) {
            Set<VehicleType> vTypes = make.getVehicleTypes();

            for (VehicleType v : vTypes) {
                v.getMakes().remove(make);
            }
            make.getVehicleTypes().clear();
        }

        if (!make.getModels().isEmpty()) {
            Set<Model> models = make.getModels();

            for (Model m : models) {
                m.setMake(null);
            }
            make.getModels().clear();
        }

        makeRepository.delete(make);
    }

    // attach vehicle type to make (bidirectional managed)
    @Transactional
    public void addVehicleType(Long makeId, Long vehicleTypeId) {
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("No Make found with id: " + makeId, "makeService"));
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("VehicleType not found: " + vehicleTypeId, "MakeService"));

        make.getVehicleTypes().add(vehicleType);
        vehicleType.getMakes().add(make);


        makeRepository.save(make);
        vehicleTypeRepository.save(vehicleType);
    }


    @Transactional
    public void removeVehicleType(Long makeId, Long vehicleTypeId) {
        Make make = getMakeById(makeId);
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("VehicleType not found: " + vehicleTypeId, "MakeService"));

        boolean removed = make.getVehicleTypes().remove(vehicleType);
        if (removed && vehicleType.getMakes() != null) {
            vehicleType.getMakes().remove(make);
            makeRepository.save(make);
            vehicleTypeRepository.save(vehicleType);
        }
    }

    // Add Model in Make
    @Transactional
    public Model addModelToMake(Long makeId, String modelName) {
        if (modelName == null || modelName.isBlank())
            throw new CustomInvalidArgumentException("Model name cannot be blank", "MakeService");

        Make make = getMakeById(makeId);

        // Duplicate check within make
        boolean exists = make.getModels().stream().anyMatch(model -> model.getName().equalsIgnoreCase(modelName));
        if (exists) {
            throw new CustomTargetAlreadyExistsException("Model already exists for this make: " + modelName, "MakeService");
        }

        Model model = new Model();
        model.setName(modelName);
        model.setMake(make);

        model = modelRepository.save(model);

        // maintain bidirectional relationship
        make.getModels().add(model);
        makeRepository.save(make);

        return model;
    }

    @Transactional
    public void removeModelFromMake(Long makeId, Long modelId) {
        Make make = getMakeById(makeId);
        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Model not found: " + modelId, "MakeService"));

        if (!model.getMake().getId().equals(make.getId())) {
            throw new CustomInvalidArgumentException("Model does not belong to given Make", "MakeService");
        }

        make.getModels().remove(model);
        modelRepository.delete(model);
    }
}

