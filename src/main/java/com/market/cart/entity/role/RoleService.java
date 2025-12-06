package com.market.cart.entity.role;

import com.market.cart.entity.role.capability.Capability;
import com.market.cart.entity.role.capability.CapabilityRepository;

import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

        return roleMapper.toReadOnlyDTO(savedRole);
    }

    public ResponseEntity<?> removeRole(Long id) {

       Role role = roleRepository.findById(id)
               .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+id, "roleService"));
       role.getCapabilities().clear();
       role.getUsers().clear();
       roleRepository.delete(role);
       return ResponseEntity.ok("Role with id: "+id+" removed successfully");
    }

    public void addCapabilityToRole(Long roleId, Long capabilityId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "roleService"));
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: "+capabilityId, "roleService"));
        role.addCapability(capability);
    }

    public void removeCapabilityFromRole(Long roleId, Long capabilityId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "roleService"));
        Capability capability = capabilityRepository.findById(capabilityId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Capability not found with id: "+capabilityId, "roleService"));
        role.removeCapability(capability);
    }
}
