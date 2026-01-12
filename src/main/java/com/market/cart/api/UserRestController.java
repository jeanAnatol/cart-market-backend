package com.market.cart.api;

import com.market.cart.entity.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for user-related operations.
 *
 * <p>
 * Provides endpoints for:
 * </p>
 * <ul>
 *     <li>User registration</li>
 *     <li>User profile updates</li>
 *     <li>User retrieval</li>
 *     <li>User deletion</li>
 *     <li>Fetching the currently authenticated user</li>
 * </ul>
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "Create, update, delete, and manage users")
public class UserRestController {

    private final UserService userService;

    /**
     * Creates a new user account.
     *
     * <p>
     * This endpoint is typically used for user registration.
     * </p>
     *
     * @param insertDTO user registration data
     * @return created user (read-only)
     */
    @Operation(summary = "Create new User")
    @PostMapping("/new")
    public ResponseEntity<UserReadOnlyDTO> newUser(
            @Valid @RequestBody UserInsertDTO insertDTO) {

        UserReadOnlyDTO  savedUser = userService.saveUser(insertDTO);
        log.info("New user account with [username = {}]", insertDTO.username());

        return ResponseEntity.ok(savedUser);
    }

    /**
     * Updates the currently authenticated user's profile.
     *
     * <p>
     * The user identity is resolved from the JWT token contained in the request.
     * </p>
     *
     * @param updateDTO updated user data
     * @param request HTTP request containing authentication header
     * @return updated user information
     */
    @Operation(summary = "Update user")
    @PostMapping("/update")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @Valid @RequestBody UserUpdateDTO updateDTO, HttpServletRequest request) {
        UserReadOnlyDTO updatedUser = userService.updateUser(updateDTO, request);
        log.info("Updated user profile [username = {}]", updatedUser.getUsername());

        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Retrieves a user by UUID.
     *
     * <p>
     * Intended for administrative or privileged access.
     * </p>
     *
     * @param uuid unique user identifier
     * @return user details
     */
    @Operation(summary = "Find User by UUID")
    @GetMapping("/find/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getByUUID(
            @PathVariable String uuid) {
        UserReadOnlyDTO readOnlyDTO = userService.getUserByUuid(uuid);
        log.info("Fetching user by [UUID = {}]", uuid);

        return ResponseEntity.ok(readOnlyDTO);
    }

    /**
     * Deletes a user by UUID.
     *
     * <p>
     * This is a destructive operation and should be restricted.
     * </p>
     *
     * @param uuid unique user identifier
     * @return confirmation message
     */
    @Operation(summary = "Delete User by UUID")
    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String uuid) {
        userService.deleteUser(uuid);
        log.info("Deleted user with [UUID = {}]", uuid);

        return ResponseEntity.ok("User with uuid: "+uuid+" removed successfully");
    }
    /**
     * Retrieves the currently authenticated user.
     *
     * <p>
     * Uses Spring Security's {@link Authentication} object.
     * </p>
     *
     * @param authentication security context authentication
     * @return current user details
     */
    @Operation(summary = "Get current User from Authentication")
    @GetMapping("/current-user")
    public UserReadOnlyDTO getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        log.debug("Fetching user [username = {}] from security context", username);

        return userService.getUserByUsername(username);
    }
}
