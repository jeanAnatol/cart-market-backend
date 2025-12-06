package com.market.cart.entity.role.capability;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CapabilityMapper {

    public Capability toCapability(CapabilityInsertDTO capInsDTO) {

        Capability capability = new Capability();

        capability.setName(capInsDTO.name());
        capability.setDescription(capInsDTO.description());

        return capability;
    }

    public CapabilityReadOnlyDTO toReadOnlyDTO(Capability capability) {

        CapabilityReadOnlyDTO capabilityReadOnlyDTO = new CapabilityReadOnlyDTO();

        capabilityReadOnlyDTO.setName(capability.getName());
        capabilityReadOnlyDTO.setDescription(capability.getDescription());
        capabilityReadOnlyDTO.setRoles(capability.getRoles());

        return capabilityReadOnlyDTO;
    }

}
