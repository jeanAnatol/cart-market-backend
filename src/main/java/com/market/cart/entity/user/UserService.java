package com.market.cart.entity.user;

import com.market.cart.authentication.JwtService;
import com.market.cart.entity.role.Role;
import com.market.cart.entity.role.RoleRepository;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import com.market.cart.validation.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserValidator userValidator;
    public final JwtService jwtService;

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

    public UserReadOnlyDTO updateUser(UserUpdateDTO updateDTO, HttpServletRequest request) {

        String username = jwtService.getUsernameFromToken(request);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with username: "+username, "userService"));
        userValidator.validateUpdateDTO(updateDTO, user);

        userRepository.save(userMapper.updateUser(updateDTO, user));

        return userMapper.toReadOnlyDTO(user);
    }

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

    public void removeRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with id: "+userId, "userService"));
        user.setRole(null);
    }

    public void deleteUser(String uuid) {
        if (userRepository.findByUuid(uuid).isEmpty()) {
            throw new CustomTargetNotFoundException("User not found with uuid: "+uuid, "userService");
        }
        userRepository.deleteByUuid(uuid);
    }

    public Role defaultRole() {
        return roleRepository.findByName("USER").
                orElseThrow(() -> new CustomTargetNotFoundException("No Role found with name: USER", "userService"));
    }

}
