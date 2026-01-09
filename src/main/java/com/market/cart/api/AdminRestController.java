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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
//@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping("/api/admin")

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

    private final ModelMapper modelMapper;
    private final VehicleTypeMapper vehicleTypeMapper;

    ///    ======================
    ///    VEHICLE TYPE ENDPOINTS
    ///    ======================

    /// Create vehicle type
    @Operation(summary = "Create a new vehicle type")               /// ΔΟΥΛΕΥΕΙ
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle type created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/vehicle-types/add")
    public ResponseEntity<VehicleTypeReadOnlyDTO> createVehicleType(
            @Valid @RequestPart("data") VehicleTypeInsertDTO vTypeInsDTO) {

        VehicleTypeReadOnlyDTO readOnlyDTO = vehicleTypeService.createVehicleType(vTypeInsDTO);
        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "Update name of a vehicle type")             /// ΔΟΥΛΕΥΕΙ
    @PostMapping("/vehicle-types/{typeId}/update-name/{name}")
    public ResponseEntity<VehicleTypeReadOnlyDTO> updateVehicleType(
            @PathVariable Long typeId,
            @PathVariable String name
    ){
        VehicleTypeReadOnlyDTO updated = vehicleTypeService.updateVehicleType(typeId, name);
        return ResponseEntity.ok(updated);
    }

    /// All Vehicle Types
    @Operation(summary = "Get all vehicle types")               /// ΔΟΥΛΕΥΕΙ
    @GetMapping("/vehicle-types/all")
    public ResponseEntity<Set<VehicleTypeReadOnlyDTO>> getAllVehicleTypes() {
        return ResponseEntity.ok(
                vehicleTypeService.getAllVehicleTypes());
    }

    /// Delete Vehicle Type
    @Operation(summary = "Delete a vehicle type by name.")          /// ΔΟΥΛΕΥΕΙ
    @DeleteMapping("/vehicle-types/delete/{name}")
    public ResponseEntity<?> deleteVehicleType(@PathVariable String name) {
        vehicleTypeService.deleteVehicleType(name);
        return ResponseEntity.ok("VehicleType deleted successfully");
    }

    ///    ==============
    ///    MAKE ENDPOINTS
    ///    ==============

    /// Create new Make   -   VehicleTypeID must be provided
    @Operation(summary = "Create a new Make")               /// ΔΟΥΛΕΥΕΙ TODO: ftiaxe to readOnlyDTO na fernei markes
    @PostMapping("/makes/create")
    public ResponseEntity<MakeReadOnlyDTO> createMake(
            @Valid @RequestPart("data") MakeInsertDTO insertDTO) {
        MakeReadOnlyDTO makeReadOnlyDTO = makeService.createMake(insertDTO);
        return ResponseEntity.ok(makeReadOnlyDTO);
    }

    /// Get All Makes
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
        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Add vehicle type to make
    @Operation(summary = "Add vehicle type to Make")
    @PostMapping("/makes/{makeId}/add-vehicle-type/{typeId}")
    public ResponseEntity<?> addVehicleTypeFromMake(
            @PathVariable Long makeId,
            @PathVariable Long typeId) {

        makeService.addVehicleType(makeId, typeId);
        return ResponseEntity.ok("VehicleType added to Make");
    }


    /// Remove Vehicle Type From Make
    @Operation(summary = "Remove vehicle type from a Make")
    @DeleteMapping("/makes/{makeId}/remove-vehicle-type/{typeId}")
    public ResponseEntity<?> deleteVehicleTypeFromMake(
            @PathVariable Long makeId,
            @PathVariable Long typeId) {

        makeService.removeVehicleType(makeId, typeId);
        return ResponseEntity.ok("VehicleType deleted from Make");
    }



    /// Delete Make
    @Operation(summary = "Delete make")
    @DeleteMapping("/makes/delete/{makeId}")
    public ResponseEntity<?> deleteMake(@PathVariable Long makeId) {
        makeService.deleteMake(makeId);
        return ResponseEntity.ok("Make deleted successfully");
    }

    ///    ===============
    ///    MODEL ENDPOINTS
    ///    ===============

    /// Add model
    @Operation(summary = "Add a new model")         /// ΔΟΥΛΕΥΕΙ
    @PostMapping("/models/new")
    public ResponseEntity<ModelReadOnlyDTO> addModel(
            @Valid @RequestPart("data") ModelInsertDTO modelInsertDTO) {
        ModelReadOnlyDTO readOnlyDTO = modelService.addModel(modelInsertDTO);
        return ResponseEntity.ok(readOnlyDTO);
    }

    /// All models
    @Operation(summary = "Get all models")
    @GetMapping("/models/all")                      /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<Set<ModelReadOnlyDTO>> getAllModels() {
        Set<ModelReadOnlyDTO> models = modelService.getAllModels();
        return ResponseEntity.ok(models);
    }

    /// Update Model
    @Operation(summary = "Update model name")
    @PutMapping("/models/update")                   /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<ModelReadOnlyDTO> updateModel(
            @RequestPart("data") ModelUpdateDTO updateDTO
            ) {
        ModelReadOnlyDTO readOnlyDTO = modelService.updateModel(updateDTO);
        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Delete Model
    @Operation(summary = "Delete model")
    @DeleteMapping("/models/delete/{modelId}")                   /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> deleteModel(@PathVariable Long modelId) {
        modelService.deleteModel(modelId);
        return ResponseEntity.ok("Model deleted successfully");
    }

    ///     =================================
    ///     FUEL TYPES
    ///     =================================


    @Operation(summary = "Add fuel type")
    @PostMapping("/fuel-types/add")                   /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<FuelTypeReadOnlyDTO> addFuelType(
            @Valid @RequestPart("data")FuelTypeInsertDTO insertDTO) {

        FuelTypeReadOnlyDTO readOnlyDTO = fuelTypeService.addFuelType(insertDTO);

        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "Get all fuel Types")
    @GetMapping("/fuel-types/all")
    public ResponseEntity<Set<FuelTypeReadOnlyDTO>> allFuelTypes() {
        Set<FuelTypeReadOnlyDTO> allTypes = fuelTypeService.getAllFuelTypes();
        return ResponseEntity.ok(allTypes);
    }

    @Operation(summary = "Change fuel type name")
    @PostMapping("/fuel-types/{typeId}/update/{name}")                                      /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<FuelTypeReadOnlyDTO> updateFuelTypeName(
            @PathVariable Long typeId,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(fuelTypeService.changeTypeName(typeId, name));
    }

    @Operation(summary = "Delete fuel type")
    @DeleteMapping("/fuel-types/delete/{typeId}")                                           /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> updateFuelTypeName(
            @PathVariable Long typeId
            ) {

        fuelTypeService.removeFuelType(typeId);

        return ResponseEntity.ok("Fuel type removed successfully");
    }


    ///     =================================
    ///     ROLES AND CAPABILITIES MANAGEMENT
    ///     =================================


    /// Insert new Role
    @Operation(summary = "Create new role")
    @PostMapping("/roles/new")                                                              /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<RoleReadOnlyDTO> addRole(
            @Valid @RequestPart("data") RoleInsertDTO roleInsertDTO) {
        RoleReadOnlyDTO roleReadOnlyDTO = roleService.addRole(roleInsertDTO);
        return ResponseEntity.ok(roleReadOnlyDTO);
    }

    /// Remove Role
    @Operation(summary = "Delete a role")
    @PostMapping("/roles/delete/{roleId}")                                                    /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> removeRole(@PathVariable Long roleId) {
        roleService.removeRole(roleId);
        return ResponseEntity.ok("Role removed with id: "+roleId);
    }

    /// Insert Capability to Role
    @Operation(summary = "Insert capability to role")
    @PostMapping("/roles/{roleId}/add-capability/{capabilityId}")                             /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> addCapabilityToRole(
            @PathVariable Long roleId,
            @PathVariable Long capabilityId ) {
        roleService.addCapabilityToRole(roleId, capabilityId);
        return ResponseEntity.ok("Capability added to Role");
    }

    /// Remove Capability from Role
    @Operation(summary = "Remove capability from role")
    @PostMapping("/roles/{roleId}/remove-capability/{capabilityId}")                           /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> removeCapabilityFromRole(
            @PathVariable Long roleId,
            @PathVariable Long capabilityId ) {
        roleService.removeCapabilityFromRole(roleId, capabilityId);
        return ResponseEntity.ok("Capability removed from role successfully");
    }

//    /// Insert Role to User
//    @Operation(summary = "Insert role to a User")
//    @PostMapping("/users/{userId}/add-role/{roleId}")
//    public ResponseEntity<?> addRoleToUser(
//            @PathVariable Long userId,
//            @PathVariable Long roleId) {
//        userService.addRoleToUser(userId, roleId);
//        return ResponseEntity.ok("Role added to user successfully");
//    }

    /// Change Role for User
    @Operation(summary = "Change role for a User")
    @PostMapping("/users/{userId}/change-role/{roleId}")                                                /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<?> changeRoleToUser(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        userService.changeRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role changed for user with id: "+ userId +" successfully");
    }

//    /// Remove Role from User
//    @Operation(summary = "Remove role from User")
//    @PostMapping("/users/{userId}/remove-role")
//    public ResponseEntity<?> changeRoleToUser(
//            @PathVariable Long userId) {
//        userService.removeRole(userId);
//        return ResponseEntity.ok("Role removed successfully for user with id: " + userId);
//    }

    /// Insert capability
    @Operation(summary = "Insert new Capability")
    @PostMapping("/capabilities/new")                                             /// ΔΟΥΛΕΥΕΙ
    public ResponseEntity<CapabilityReadOnlyDTO> addCapability(
            @Valid @RequestPart("data")CapabilityInsertDTO capabilityInsertDTO) {
        CapabilityReadOnlyDTO readOnlyDTO = capabilityService.addCapability(capabilityInsertDTO);
        return ResponseEntity.ok(readOnlyDTO);
    }

    /// Remove Capability
    @Operation(summary = "Remove a capability")                                  /// ΔΟΥΛΕΥΕΙ
    @DeleteMapping("/capabilities/delete/{capabilityId}")
    public ResponseEntity<?> removeCapability (
            @PathVariable Long capabilityId) {
        capabilityService.removeCapability(capabilityId);
        return ResponseEntity.ok("Capability with id: "+capabilityId+ " removed successfully");
    }
}
