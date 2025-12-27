package com.market.cart.entity.fueltype;

import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    public FuelTypeReadOnlyDTO addFuelType(FuelTypeInsertDTO insertDTO) {

        FuelType fuelType = new FuelType(null, insertDTO.name());
        return toReadIOnlyDTO(fuelTypeRepository.save(fuelType));
    }


    public Set<FuelTypeReadOnlyDTO> getAllFuelTypes() {

        Set<FuelType> allFuelTypes = fuelTypeRepository.findAll().stream().collect(Collectors.toUnmodifiableSet());
        Set<FuelTypeReadOnlyDTO> allToDTO = new HashSet<>();

        for (FuelType f : allFuelTypes) {
            allToDTO.add(toReadIOnlyDTO(f));
        }
        return allToDTO;
    }


    public FuelTypeReadOnlyDTO changeTypeName(Long typeId, String name) {

        FuelType fuelType = fuelTypeRepository.findById(typeId)
                .orElseThrow(() -> new CustomTargetNotFoundException("No fuel Type found with id: "+typeId, "fuelTypeService"));
        fuelType.setName(name);

        return toReadIOnlyDTO(fuelTypeRepository.save(fuelType));
    }


    public void removeFuelType(Long typeId) {

        if (fuelTypeRepository.findById(typeId).isEmpty()) {
            throw new CustomTargetNotFoundException("no Fuel Type found with id: "+typeId, "fuelTypeService");
        }
        fuelTypeRepository.deleteById(typeId);
    }


    public FuelTypeReadOnlyDTO toReadIOnlyDTO (FuelType fuelType) {
        return new FuelTypeReadOnlyDTO(fuelType.getId(), fuelType.getName());
    }
}
