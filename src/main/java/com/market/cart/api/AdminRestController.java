package com.market.cart.api;

import com.market.cart.entity.fueltype.FuelTypeInsertDTO;
import com.market.cart.entity.fueltype.FuelTypeReadOnlyDTO;
import com.market.cart.entity.fueltype.FuelTypeService;
import com.market.cart.entity.make.*;
import com.market.cart.entity.model.*;
import com.market.cart.entity.role.*;
import com.market.cart.entity.role.capability.CapabilityInsertDTO;
import com.market.cart.entity.role.capability.CapabilityReadOnlyDTO;
import com.market.cart.entity.role.capability.CapabilityService;
import com.market.cart.entity.user.UserService;
import com.market.cart.entity.vehicletype.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST controller providing administrative endpoints.
 *
 * <p>
 * This controller allows administrators to manage:
 * </p>
 * <ul>
 *     <li>Vehicle types</li>
 *     <li>Makes and models</li>
 *     <li>Fuel types</li>
 *     <li>Roles and capabilities</li>
 *     <li>User role assignments</li>
 * </ul>
 *
 * <p>
 * All endpoints are restricted to users with {@code ROLE_ADMIN}.
 * </p>
 */

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Administration of Makes, Models, Vehicle Types, Roles & Capabilities")
public class AdminRestController {

    private final MakeService makeService;
    private final ModelService modelService;
    private final VehicleTypeService vehicleTypeService;
    private final RoleService roleService;
    private final UserService userService;
    private final CapabilityService capabilityService;
    private final FuelTypeService fuelTypeService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Action successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })

    ///    ======================
    ///    VEHICLE TYPE ENDPOINTS
    ///    ======================

    @Operation(summary = "Create a new vehicle type")
    @PostMapping("/vehicle-types/add")
    public ResponseEntity<VehicleTypeReadOnlyDTO> createVehicleType(
            @Valid @RequestPart("data") VehicleTypeInsertDTO vTypeInsDTO) {

        VehicleTypeReadOnlyDTO readOnlyDTO = vehicleTypeService.createVehicleType(vTypeInsDTO);
        log.info("Admin created vehicle type [name = {}]", vTypeInsDTO.name());

        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "Update name of a vehicle type")
    @PostMapping("/vehicle-types/{typeId}/update-name/{name}")
    public ResponseEntity<VehicleTypeReadOnlyDTO> updateVehicleType(
            @PathVariable Long typeId,
            @PathVariable String name
    ){
        VehicleTypeReadOnlyDTO updated = vehicleTypeService.updateVehicleType(typeId, name);
        log.info("Admin updated vehicle type [id = {}]", typeId);
        return ResponseEntity.ok(updated);
    }

    /**
     * All Vehicle Types
     */
    @Operation(summary = "Get all vehicle types")
    @GetMapping("/vehicle-types/all")
    public ResponseEntity<Set<VehicleTypeReadOnlyDTO>> getAllVehicleTypes() {

        return ResponseEntity.ok(
                vehicleTypeService.getAllVehicleTypes());
    }
    /**
    * Delete Vehicle Type
    */
    @Operation(summary = "Delete a vehicle type by name.")
    @DeleteMapping("/vehicle-types/delete/{name}")
    public ResponseEntity<?> deleteVehicleType(@PathVariable String name) {
        vehicleTypeService.deleteVehicleType(name);
        log.warn("Admin deleted vehicle type [name = {}]", name);
        return ResponseEntity.ok("VehicleType deleted successfully");
    }

    ///    ==============
    ///    MAKE ENDPOINTS
    ///    ==============

    /**
     * Create new Make   -   VehicleTypeID must be provided
     * @param insertDTO
     * @return
     */
    @Operation(summary = "Create a new Make")
    @PostMapping("/makes/create")
    public ResponseEntity<MakeReadOnlyDTO> createMake(
            @Valid @RequestPart("data") MakeInsertDTO insertDTO) {
        MakeReadOnlyDTO makeReadOnlyDTO = makeService.createMake(insertDTO);
        log.info("Admin created make [name = {}]", insertDTO.name());
        return ResponseEntity.ok(makeReadOnlyDTO);
    }

    /**
     * Get All Makes
     */
    @Operation(summary = "Get all makes")
    @GetMapping("/makes/all")
    public ResponseEntity<Set<MakeReadOnlyDTO>> getAllMakes() {
        Set<MakeReadOnlyDTO> makes = makeService.getAllMakes();
        return ResponseEntity.ok(makes);
    }

    /// Update Make
    @Operation(summary = "Update make name")
    @PutMapping("/makes/update/{id}/update-name/{name}")
    public ResponseEntity<MakeReadOnlyDTO> updateMake(
            @PathVariable Long makeId,
            @PathVariable String name) {
        MakeReadOnlyDTO readOnlyDTO = makeService.updateMake(makeId, name);
        log.info("Admin updated make [id = {}] with [name = {}]", makeId, name);
        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Add vehicle type to make
    @Operation(summary = "Add vehicle type to Make")
    @PostMapping("/makes/{makeId}/add-vehicle-type/{typeId}")
    public ResponseEntity<?> addVehicleTypeFromMake(
            @PathVariable Long makeId,
            @PathVariable Long typeId) {

        makeService.addVehicleType(makeId, typeId);
        log.info("Admin added vehicleType [name = {}] to make [name = {}]", typeId,makeId);
        return ResponseEntity.ok("VehicleType added to Make");
    }

    /// Remove Vehicle Type From Make
    @Operation(summary = "Remove vehicle type from a Make")
    @DeleteMapping("/makes/{makeId}/remove-vehicle-type/{typeId}")
    public ResponseEntity<?> deleteVehicleTypeFromMake(
            @PathVariable Long makeId,
            @PathVariable Long typeId) {

        makeService.removeVehicleType(makeId, typeId);
        log.info("Admin removed vehicleType [id = {}] from make [id = {}]", typeId, makeId);
        return ResponseEntity.ok("VehicleType deleted from Make");
    }

    /// Delete Make
    @Operation(summary = "Delete make")
    @DeleteMapping("/makes/delete/{makeId}")
    public ResponseEntity<?> deleteMake(@PathVariable Long makeId) {
        makeService.deleteMake(makeId);

        log.warn("Admin deleted make [name = {}]", makeId);
        return ResponseEntity.ok("Make deleted successfully");
    }

    ///    ===============
    ///    MODEL ENDPOINTS
    ///    ===============

    /// Add model
    @Operation(summary = "Add a new model")
    @PostMapping("/models/new")
    public ResponseEntity<ModelReadOnlyDTO> addModel(
            @Valid @RequestPart("data") ModelInsertDTO modelInsertDTO) {
        ModelReadOnlyDTO readOnlyDTO = modelService.addModel(modelInsertDTO);
        log.info("Admin adding model [name = {}]", modelInsertDTO.name());

        return ResponseEntity.ok(readOnlyDTO);
    }

    /// All models
    @Operation(summary = "Get all models")
    @GetMapping("/models/all")
    public ResponseEntity<Set<ModelReadOnlyDTO>> getAllModels() {
        Set<ModelReadOnlyDTO> models = modelService.getAllModels();
        return ResponseEntity.ok(models);
    }

    /// Update Model
    @Operation(summary = "Update model name")
    @PutMapping("/models/update")
    public ResponseEntity<ModelReadOnlyDTO> updateModel(
            @RequestPart("data") ModelUpdateDTO updateDTO
            ) {
        ModelReadOnlyDTO readOnlyDTO = modelService.updateModel(updateDTO);
        log.info("Admin updating model [id={}]", updateDTO.modelId());

        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Delete Model
    @Operation(summary = "Delete model")
    @DeleteMapping("/models/delete/{modelId}")
    public ResponseEntity<?> deleteModel(@PathVariable Long modelId) {
        modelService.deleteModel(modelId);
        log.warn("Admin deleted model [id={}]", modelId);

        return ResponseEntity.ok("Model deleted successfully");
    }

    ///     =================================
    ///     FUEL TYPES
    ///     =================================

    @Operation(summary = "Add fuel type")
    @PostMapping("/fuel-types/add")
    public ResponseEntity<FuelTypeReadOnlyDTO> addFuelType(
            @Valid @RequestPart("data")FuelTypeInsertDTO insertDTO) {

        FuelTypeReadOnlyDTO readOnlyDTO = fuelTypeService.addFuelType(insertDTO);
        log.info("Admin added fuel type [name = {}]", insertDTO.name());

        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "Get all fuel Types")
    @GetMapping("/fuel-types/all")
    public ResponseEntity<Set<FuelTypeReadOnlyDTO>> allFuelTypes() {
        Set<FuelTypeReadOnlyDTO> allTypes = fuelTypeService.getAllFuelTypes();
        return ResponseEntity.ok(allTypes);
    }

    @Operation(summary = "Change fuel type name")
    @PostMapping("/fuel-types/{typeId}/update/{name}")
    public ResponseEntity<FuelTypeReadOnlyDTO> updateFuelTypeName(
            @PathVariable Long typeId,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(fuelTypeService.changeTypeName(typeId, name));
    }

    @Operation(summary = "Delete fuel type")
    @DeleteMapping("/fuel-types/delete/{typeId}")
    public ResponseEntity<?> updateFuelTypeName(
            @PathVariable Long typeId
            ) {

        fuelTypeService.removeFuelType(typeId);
        log.info("Admin deleted fuel type [id = {}]", typeId);

        return ResponseEntity.ok("Fuel type removed successfully");
    }

    ///     =================================
    ///     ROLES AND CAPABILITIES MANAGEMENT
    ///     =================================

    /// Insert new Role
    @Operation(summary = "Create new role")
    @PostMapping("/roles/new")
    public ResponseEntity<RoleReadOnlyDTO> addRole(
            @Valid @RequestPart("data") RoleInsertDTO roleInsertDTO) {
        RoleReadOnlyDTO roleReadOnlyDTO = roleService.addRole(roleInsertDTO);
        log.warn("Admin creating new role [name = {}]", roleInsertDTO.name());

        return ResponseEntity.ok(roleReadOnlyDTO);
    }

    /// Remove Role
    @Operation(summary = "Delete a role")
    @PostMapping("/roles/delete/{roleId}")
    public ResponseEntity<?> removeRole(@PathVariable Long roleId) {
        roleService.removeRole(roleId);
        log.warn("Admin deleted role [id = {}]", roleId);

        return ResponseEntity.ok("Role removed with id: "+roleId);
    }

    /// Insert Capability to Role
    @Operation(summary = "Insert capability to role")
    @PostMapping("/roles/{roleId}/add-capability/{capabilityId}")
    public ResponseEntity<?> addCapabilityToRole(
            @PathVariable Long roleId,
            @PathVariable Long capabilityId ) {
        roleService.addCapabilityToRole(roleId, capabilityId);
        log.warn("Admin added capability [id = {}] to role [id = {}]", capabilityId, roleId);

        return ResponseEntity.ok("Capability added to Role");
    }

    /// Remove Capability from Role
    @Operation(summary = "Remove capability from role")
    @PostMapping("/roles/{roleId}/remove-capability/{capabilityId}")
    public ResponseEntity<?> removeCapabilityFromRole(
            @PathVariable Long roleId,
            @PathVariable Long capabilityId ) {
        roleService.removeCapabilityFromRole(roleId, capabilityId);
        log.warn("Admin removed capability [id = {}] from role [id = {}]", capabilityId, roleId);

        return ResponseEntity.ok("Capability removed from role successfully");
    }

    /// Change Role for User
    @Operation(summary = "Change role for a User")
    @PostMapping("/users/{userId}/change-role/{roleId}")
    public ResponseEntity<?> changeRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userService.changeRoleToUser(userId, roleId);
        log.warn("Admin changed role [id = {}] to user [id = {}]", roleId, userId);

        return ResponseEntity.ok("Role changed for user with id: "+ userId +" successfully");
    }

    /// Insert capability
    @Operation(summary = "Insert new Capability")
    @PostMapping("/capabilities/new")
    public ResponseEntity<CapabilityReadOnlyDTO> addCapability(
            @Valid @RequestPart("data")CapabilityInsertDTO capabilityInsertDTO) {
        CapabilityReadOnlyDTO readOnlyDTO = capabilityService.addCapability(capabilityInsertDTO);

        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Remove Capability
    @Operation(summary = "Remove a capability")
    @DeleteMapping("/capabilities/delete/{capabilityId}")
    public ResponseEntity<?> removeCapability (
            @PathVariable Long capabilityId) {
        capabilityService.removeCapability(capabilityId);
        return ResponseEntity.ok("Capability with id: "+capabilityId+ " removed successfully");
    }
}
