package com.market.cart.entity.fueltype;

import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing fuel types.
 * <ul>Provides:</ul>
 * <li>Add Fuel Type</li>
 * <li>Fetch all Fuel Types</li>
 * <li>Change the name of a Fuel Type</li>
 * <li>Remove a Fuel Type</li>
 * <li>Return a readOnlyDTO for a Fuel Type</li>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    public FuelTypeReadOnlyDTO addFuelType(FuelTypeInsertDTO insertDTO) {

        FuelType fuelType = new FuelType(null, insertDTO.name());
        log.info("New Fuel Type created with name: {}", insertDTO.name());
        return toReadIOnlyDTO(fuelTypeRepository.save(fuelType));
    }

    public Set<FuelTypeReadOnlyDTO> getAllFuelTypes() {

        Set<FuelType> allFuelTypes = fuelTypeRepository.findAll().stream().collect(Collectors.toUnmodifiableSet());
        Set<FuelTypeReadOnlyDTO> allToDTO = new HashSet<>();

        for (FuelType f : allFuelTypes) {
            allToDTO.add(toReadIOnlyDTO(f));
        }
        log.info("{} Fuel Types returned.", allToDTO.size());
        return allToDTO;
    }

    public FuelTypeReadOnlyDTO changeTypeName(Long typeId, String name) {

        String oldName;
        FuelType fuelType = fuelTypeRepository.findById(typeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("No fuel Type found with id: "+typeId, "fuelTypeService"));
        oldName = fuelType.getName();
        fuelType.setName(name);

        log.debug("Fuel Type with id: {}, changed name from: {} to: {}", typeId, oldName, name);
        return toReadIOnlyDTO(fuelTypeRepository.save(fuelType));
    }

    public void removeFuelType(Long typeId) {

        String fuelName;
        FuelType fuelType = fuelTypeRepository.findById(typeId)
                        .orElseThrow(() -> new CustomTargetNotFoundException("no Fuel Type found with id: "+typeId, "fuelTypeService"));
        fuelName = fuelType.getName();
        fuelTypeRepository.deleteById(typeId);
        log.info("Fuel Type removed with id: {} and name: {}", typeId, fuelName);
    }

    public FuelTypeReadOnlyDTO toReadIOnlyDTO (FuelType fuelType) {
        return new FuelTypeReadOnlyDTO(fuelType.getId(), fuelType.getName());
    }
}
