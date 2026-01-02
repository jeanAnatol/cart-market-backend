package com.market.cart.api;

import com.market.cart.entity.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Create, update, delete, and manage users")
public class UserRestController {

    private final UserService userService;



    @Operation(summary = "Create new User")
    @PostMapping("/new")
    public ResponseEntity<UserReadOnlyDTO> newUser(
            @Valid @RequestBody UserInsertDTO uInsDTO) {

        UserReadOnlyDTO  savedUser = userService.saveUser(uInsDTO);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "Update a user using UUID")
    @PostMapping("/update/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @Valid @RequestPart("uuid") String uuid,
            @Valid @RequestPart("data") UserInsertDTO uInsDTO) {
        UserReadOnlyDTO updatedUser = userService.updateUser(uuid, uInsDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Find User by UUID")
    @GetMapping("/find/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getByUUID(
            @PathVariable String uuid) {
        UserReadOnlyDTO readOnlyDTO = userService.getUserByUuid(uuid);
        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "Delete User by UUID")
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok("User with uuid: "+uuid+" removed successfully");
    }
}
