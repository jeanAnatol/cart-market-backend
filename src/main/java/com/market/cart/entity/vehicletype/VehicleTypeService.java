package com.market.cart.entity.vehicletype;

import com.market.cart.entity.make.*;
import com.market.cart.entity.model.Model;
import com.market.cart.entity.model.ModelReadOnlyDTO;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for managing {@link VehicleType} entities.
 *
 * <p>
 * Handles creation, update, retrieval, deletion, and mapping of
 * vehicle types along with their associated {@link Make} and {@link Model}.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final VehicleTypeMapper vehicleTypeMapper;
    private final MakeMapper makeMapper;
    private final MakeService makeService;

    /**
     * Creates a new vehicle type.
     */
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

        log.info("VehicleType created with name: {}", vehicleType.getName());
        return vehicleTypeMapper.toReadOnlyDTO(vehicleType, getMakeDTO(vehicleType));
    }

    /**
     * Updates the name of a Vehicle Type
     * @param typeId
     * @param name
     * @return
     */
    public VehicleTypeReadOnlyDTO updateVehicleType(Long typeId, String name) {

        VehicleType vehicleType = vehicleTypeRepository.findById(typeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("No vehicle type found with ID: " + typeId, "vehicleTypeService"));
        vehicleType.setName(name);

        VehicleType saved = vehicleTypeRepository.save(vehicleType);

        log.info("VehicleType updated: id={}, newName={}", typeId, name);
        return vehicleTypeMapper.toReadOnlyDTO(saved, getMakeDTO(vehicleType));
    }

    @Transactional(readOnly = true)
    public Set<VehicleTypeReadOnlyDTO> getAllVehicleTypes() {

        Set<VehicleType> allTypes = vehicleTypeRepository.findAll()
                .stream().collect(Collectors.toUnmodifiableSet());

        Set<VehicleTypeReadOnlyDTO> vTypesDTO = new HashSet<>();

        for (VehicleType v : allTypes) {
            Set<MakeReadOnlyDTO> makes = getMakeDTO(v);
            VehicleTypeReadOnlyDTO vType = vehicleTypeMapper.toReadOnlyDTO(v, makes);
            vTypesDTO.add(vType);
        }
        log.debug("Fetched {} vehicle types", vTypesDTO.size());
        return vTypesDTO;
    }

    /**
     * Deletes a vehicle type by name.
     *
     * <p>
     * Ensures removal of bidirectional relationships with {@link Make}s
     * before deletion.
     * </p>
     *
     * @param name vehicle type name
     */
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
        log.warn("VehicleType deleted with name: {}", name);
    }

    /**
     * Maps a {@link VehicleType}'s associated makes to read-only DTOs.
     * @return set of make DTOs
     */
    public Set<MakeReadOnlyDTO> getMakeDTO(VehicleType vehicleType) {

        Set<MakeReadOnlyDTO> makeDTO = new HashSet<>();
        for (Make m : vehicleType.getMakes()) {
            makeDTO.add(makeMapper.toReadOnlyDTO(m, makeService.getModelDTO(m)));
        }
        return makeDTO;
    }
}
