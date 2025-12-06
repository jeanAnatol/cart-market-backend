package com.market.cart.entity.role.capability;

import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;

import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.VolatileImage;

@Service
@RequiredArgsConstructor
public class CapabilityService {

    private final CapabilityMapper capabilityMapper;
    private final CapabilityRepository capabilityRepository;

    public CapabilityReadOnlyDTO addCapability(CapabilityInsertDTO capInsDTO) {

        if (capabilityRepository.findByName(capInsDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Capability with name: "+capInsDTO.name()+ " already exists.", "capabilityService");
        }
        Capability capability = capabilityMapper.toCapability(capInsDTO);
        Capability savedCapability = capabilityRepository.save(capability);
        return capabilityMapper.toReadOnlyDTO(savedCapability);
    }

    public void removeCapability(Long capabilityId) {
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: " + capabilityId, "capabilityService"));
        capabilityRepository.deleteById(capabilityId);
    }
}
