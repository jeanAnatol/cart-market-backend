package com.market.cart.entity.user;

import com.market.cart.authentication.JwtService;
import com.market.cart.entity.role.Role;
import com.market.cart.entity.role.RoleRepository;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import com.market.cart.validation.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user management operations.
 *
 * <p>
 * Handles user creation, retrieval, updates, role assignment,
 * and deletion. Validation and security context resolution
 * are delegated to dedicated components.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserValidator userValidator;
    public final JwtService jwtService;

    /**
     * Creates and persists a new user.
     *
     * <p>
     * Applies validation rules and assigns the default {@code USER} role.
     * </p>
     *
     * @param uInsDTO user insert DTO
     * @return read-only representation of the created user
     */
    public UserReadOnlyDTO saveUser(UserInsertDTO uInsDTO) {

        userValidator.validateInsertDTO(uInsDTO);

        if (userRepository.findByUsername(uInsDTO.username()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Username \""+uInsDTO.username()+"\" already exists", "userService");
        }
        if (userRepository.findByEmail(uInsDTO.email()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Email \""+uInsDTO.email()+"\" already exists.", "userService");
        }
        User user = userMapper.toUser(uInsDTO);

        /// The role is set to USER by default
        Role role = defaultRole();

        user.setRole(role);
        role.getUsers().add(user);
        return userMapper.toReadOnlyDTO(userRepository.save(user));
    }

    public UserReadOnlyDTO getUserByID(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toReadOnlyDTO)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with id: "+id, "userService"));
    }

    public UserReadOnlyDTO getUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid)
                .map(userMapper::toReadOnlyDTO)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with UUID: "+uuid, "userService"));
    }

    public UserReadOnlyDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomTargetNotFoundException("No User found with username: "+ username, "userService"));
        return userMapper.toReadOnlyDTO(user);
    }

    /**
     * Updates the authenticated user's profile.
     *
     * <p>
     * The user is resolved from the JWT token in the request.
     * </p>
     *
     * @param updateDTO update data
     * @param request HTTP request containing JWT
     * @return updated user DTO
     */
    public UserReadOnlyDTO updateUser(UserUpdateDTO updateDTO, HttpServletRequest request) {

        String username = jwtService.getUsernameFromToken(request);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with username: "+username, "userService"));
        userValidator.validateUpdateDTO(updateDTO, user);

        userRepository.save(userMapper.updateUser(updateDTO, user));

        return userMapper.toReadOnlyDTO(user);
    }

    /**
     * Replaces the user's current role with a new one.
     */
    public void addRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with id: "+userId, "userService"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "userService"));
        user.addRole(role);
    }

    public void changeRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with id: "+userId, "userService"));
        user.getRole().removeUser(user);
        user.setRole(null);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new CustomTargetNotFoundException("Role not found with id: "+roleId, "userService"));
        user.addRole(role);
        userRepository.save(user);
    }

    /**
     * <h2>Currently not in use.</h2>
     * No infrastructure to support a user without a role in any event that this might happen.
     * Removes the role from the user and clears the association.
     */
//    public void removeRole(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with id: "+userId, "userService"));
//        Role role = user.getRole();
//        role.getUsers().remove(user);
//        user.removeRole(role);
//    }

    public void deleteUser(String uuid) {
        if (userRepository.findByUuid(uuid).isEmpty()) {
            throw new CustomTargetNotFoundException("User not found with uuid: "+uuid, "userService");
        }
        userRepository.deleteByUuid(uuid);
    }

    /**
     * Resolves the default role assigned to new users.
     *
     * @return USER role
     */
    public Role defaultRole() {
        return roleRepository.findByName("USER").
                orElseThrow(() -> new CustomTargetNotFoundException("No Role found with name: USER", "userService"));
    }

}
