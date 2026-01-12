package com.market.cart.entity.role.capability;

import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing {@link Capability} entities.
 *
 * <p>
 * Handles creation and deletion of capabilities while maintaining
 * bidirectional Role–Capability relationships.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CapabilityService {

    private final CapabilityMapper capabilityMapper;
    private final CapabilityRepository capabilityRepository;

    /**
     *  Creates a new Capability
     * @param insertDTO
     * @return
     */
    public CapabilityReadOnlyDTO addCapability(CapabilityInsertDTO insertDTO) {

        if (capabilityRepository.findByName(insertDTO.name()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Capability with name: "+insertDTO.name()+ " already exists.", "capabilityService");
        }
        Capability capability = capabilityMapper.toCapability(insertDTO);
        Capability savedCapability = capabilityRepository.save(capability);

        log.info("Capability created with name: {}", insertDTO.name());
        return capabilityMapper.toReadOnlyDTO(savedCapability);
    }

    /**
     * Deletes a Capability by its identifier.
     *
     * <p>
     * Ensures all Role associations are removed before deletion
     * to preserve referential integrity.
     * </p>
     */
    public void removeCapability(Long capabilityId) {
        String name;
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: " + capabilityId, "capabilityService"));
        name = capability.getName();

        if (!capability.getRoles().isEmpty()) {
            capability.getRoles().forEach(
                    role -> role.removeCapability(capability));
            capability.getRoles().clear();
        }

        capabilityRepository.deleteById(capabilityId);
        log.warn("Capability removed with id: {} and name: {}", capabilityId, name);
    }
}
