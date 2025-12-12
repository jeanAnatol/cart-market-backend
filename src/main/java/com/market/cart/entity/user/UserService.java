package com.market.cart.entity.user;

import com.market.cart.entity.role.Role;
import com.market.cart.entity.role.RoleRepository;
import com.market.cart.exceptions.custom.CustomTargetAlreadyExistsException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import com.market.cart.validation.UserValidator;
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

    public final PasswordEncoder passwordEncoder;

    public UserReadOnlyDTO saveUser(UserInsertDTO uInsDTO) {


        if (userRepository.findByUsername(uInsDTO.username()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Username \""+uInsDTO.username()+"\" already exists", "userService");
        }
        if (userRepository.findByEmail(uInsDTO.email()).isPresent()) {
            throw new CustomTargetAlreadyExistsException("Email \""+uInsDTO.email()+"\" already exists.", "userService");
        }
        User user = userMapper.toUser(uInsDTO);
        user.setPassword(passwordEncoder.encode(uInsDTO.password()));

        Role role;

        /// If roleId does not exist, or it is null, the role is set to USER by default
        if (uInsDTO.roleId() != null) {
            role = roleRepository.findById(uInsDTO.roleId())
                    .orElse(defaultRole());
        }else{
            role = defaultRole();
        }

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

    public UserReadOnlyDTO updateUser(String uuid, UserInsertDTO uInsDTO) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomTargetNotFoundException("User not found with uuid: "+uuid, "userService"));
        userValidator.validateInsertDTO(uInsDTO);
        userRepository.save(userMapper.updateUser(uInsDTO, user));

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
