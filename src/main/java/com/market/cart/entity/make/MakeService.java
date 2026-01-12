package com.market.cart.entity.make;

import com.market.cart.entity.model.Model;
import com.market.cart.entity.model.ModelMapper;
import com.market.cart.entity.model.ModelReadOnlyDTO;
import com.market.cart.entity.model.ModelRepository;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeReadOnlyDTO;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Service responsible for managing {@link Make} entities and their
 * relationships with {@link Model} and {@link VehicleType}.
 *
 * <p>One {@link Make} can belong to many {@link VehicleType} and can own many {@link Model}</p>
 * <p>
 * Handles creation, update, deletion, and association logic.
 * All write operations are transactional.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MakeService {

    private final MakeRepository makeRepository;
    private final ModelRepository modelRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeMapper makeMapper;
    private final ModelMapper modelMapper;

    @Transactional
    public MakeReadOnlyDTO createMake(MakeInsertDTO insertDTO) {

        if (insertDTO.name() == null || insertDTO.name().isBlank()) {
            throw new CustomInvalidArgumentException("Make name must not be blank", "MakeService");
        }

        if (makeRepository.findByName(insertDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Make with name: " +insertDTO.name()+ " already exists", "makeService");
        }

        Make make = makeMapper.toMake(insertDTO);
        Set<VehicleType> vehicleTypes = new HashSet<>();

        for (Long id : insertDTO.vehicleTypeId()) {

            VehicleType vehicleType = vehicleTypeRepository.findById(id)
                .orElseThrow(() -> new CustomTargetNotFoundException("No Vehicle Type found with this id: "+ insertDTO.vehicleTypeId(), "makeService"));

            vehicleType.getMakes().add(make);
            vehicleTypes.add(vehicleType);
        }

        make.getVehicleTypes().addAll(vehicleTypes);
        makeRepository.save(make);
        log.info("Created Make with name: {}", insertDTO.name());
        Set<ModelReadOnlyDTO> models = getModelDTO(make);

        return makeMapper.toReadOnlyDTO(make, models);
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

        Set<Make> allMakes = makeRepository.findAll().stream().collect(Collectors.toUnmodifiableSet());
        Set<MakeReadOnlyDTO> makesDTO = new HashSet<>();

        for (Make m : allMakes) {
            Set<ModelReadOnlyDTO> models = getModelDTO(m);

            MakeReadOnlyDTO make = makeMapper.toReadOnlyDTO(m, models);
            makesDTO.add(make);
        }
        return makesDTO;
    }

    /// Update make
    @Transactional
    public MakeReadOnlyDTO updateMake(Long makeId, String name) {

        String oldName;
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank or null");
        }
        Make make = makeRepository.findById(makeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Make not found with id: "+ makeId, "makeService"));

        oldName = make.getName();
        make.setName(name);
        Set<ModelReadOnlyDTO> models = getModelDTO(make);

        log.info("Make with id: {} updated name from: {} to: {}", makeId, oldName, name);
        return makeMapper.toReadOnlyDTO(makeRepository.save(make), models);
    }

    /**
     * Delete Make. Cascading action, will remove associated Models
     * @param makeId
     */
    @Transactional
    public void deleteMake(Long makeId) {

        String makeName;
        Make make = getMakeById(makeId);
        makeName= make.getName();

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

        log.warn("Removed Make with id: {} and name: {}", makeId, makeName);
        makeRepository.delete(make);
    }

    /**
     * Attach Vehicle Type to Make (bidirectionally managed)
     * @param makeId
     * @param vehicleTypeId
     */
    @Transactional
    public void addVehicleType(Long makeId, Long vehicleTypeId) {

        String makeName;
        String vehicleTypeName;

        Make make = getMakeById(makeId);
        makeName = make.getName();
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("VehicleType not found: " + vehicleTypeId, "MakeService"));
        vehicleTypeName = vehicleType.getName();

        make.getVehicleTypes().add(vehicleType);
        vehicleType.getMakes().add(make);

        makeRepository.save(make);
        vehicleTypeRepository.save(vehicleType);
        log.info("Added Vehicle Type: {} to Make: {}", vehicleTypeName, makeName);
    }


    @Transactional
    public void removeVehicleType(Long makeId, Long vehicleTypeId) {
        String makeName;
        String vehicleTypeName;

        Make make = getMakeById(makeId);
        makeName = make.getName();
        VehicleType vehicleType = vehicleTypeRepository.findById(vehicleTypeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("VehicleType not found: " + vehicleTypeId, "MakeService"));
        vehicleTypeName = vehicleType.getName();

        make.getVehicleTypes().remove(vehicleType);
        vehicleType.getMakes().remove(make);

        makeRepository.save(make);
        vehicleTypeRepository.save(vehicleType);

        log.info("Vehicle Type: {} removed from Make: {}", vehicleTypeName, makeName);
    }

    /// Add Model to Make. Not in use since Models are added from Model Service. Marked for removal
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

    /// Add Model to Make. Not in use since Models are removed from Model Service. Marked for removal
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

    public Set<ModelReadOnlyDTO> getModelDTO(Make make) {

        Set<ModelReadOnlyDTO> modelDTO = new HashSet<>();
         for (Model m : make.getModels()) {
             modelDTO.add(modelMapper.toReadOnlyDTO(m));
         }
        return modelDTO;
    }

}

