package com.market.cart.entity.role;

import com.market.cart.entity.role.capability.Capability;
import com.market.cart.entity.role.capability.CapabilityRepository;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service layer for managing {@link Role} entities.
 *
 * <p>
 * Provides operations for creating, deleting, and managing
 * role–capability associations while preserving bidirectional integrity.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    private final CapabilityRepository capabilityRepository;


    public RoleReadOnlyDTO addRole(RoleInsertDTO roleInsertDTO) {

        if (roleRepository.existsByName(roleInsertDTO.name())) {
            throw new CustomTargetAlreadyExistsException("Role already exists with name: "+roleInsertDTO.name(), "roleService");
        }
        Role role = roleMapper.toRole(roleInsertDTO);
        Role savedRole = roleRepository.save(role);

        log.info("New Role added with name: {}", roleInsertDTO.name());
        return roleMapper.toReadOnlyDTO(savedRole);
    }

    /**
     * Deletes a Role by id.
     *
     * <p>
     * Detaches all associated users and capabilities before deletion
     * to ensure referential integrity.
     * </p>
     *
     * @param id ID of the role to delete
     * @return HTTP response indicating successful deletion
     */
    public ResponseEntity<?> removeRole(Long id) {

        String name;
       Role role = roleRepository.findById(id)
               .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+id, "roleService"));

       name = role.getName();
       if (!role.getCapabilities().isEmpty()) {
           role.getCapabilities().forEach(
                   capability -> capability.getRoles().remove(role));
           role.getCapabilities().clear();
       }

       if(!role.getUsers().isEmpty()) {
           role.getUsers().forEach(user -> user.setRole(null));
           role.getUsers().clear();
       }

       roleRepository.delete(role);
       log.warn("Role removed with id: {} and name: {}", id, name);

       return ResponseEntity.ok("Role with id: "+id+" and name: "+name+" removed successfully");
    }

    /**
     * Attaches a Capability to a Role.
     */
    public void addCapabilityToRole(Long roleId, Long capabilityId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "roleService"));
        String roleName = role.getName();
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: "+capabilityId, "roleService"));
        String capName = capability.getName();
        role.addCapability(capability);
        capability.getRoles().add(role);
        roleRepository.save(role);

        log.info("Capability '{}' added to Role '{}'",capName, roleName);
    }

    /**
     * Removes a Capability from a Role.
     */
    public void removeCapabilityFromRole(Long roleId, Long capabilityId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "roleService"));
        String roleName = role.getName();
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: "+capabilityId, "roleService"));
        String capName = capability.getName();

        capability.getRoles().remove(role);
        role.removeCapability(capability);

        roleRepository.save(role);
        log.warn("Capability '{}' removed from Role '{}'", capName, roleName);
    }
}
