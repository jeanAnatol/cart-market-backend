package com.market.cart.api;

import com.market.cart.entity.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "Update user")
    @PostMapping("/update")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @Valid @RequestBody UserUpdateDTO uInsDTO, HttpServletRequest request) {
        UserReadOnlyDTO updatedUser = userService.updateUser(uInsDTO, request);
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

    @Operation(summary = "Get current User from Authentication")
    @GetMapping("/current-user")
    public UserReadOnlyDTO getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }
}
