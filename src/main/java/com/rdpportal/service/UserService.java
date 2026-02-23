package com.rdpportal.service;

import com.rdpportal.dto.CreateUserRequest;
import com.rdpportal.dto.UserDto;
import com.rdpportal.model.Role;
import com.rdpportal.model.User;
import com.rdpportal.repository.UserMachineAssignmentRepository;
import com.rdpportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMachineAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMachineAssignmentRepository assignmentRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }

    public UserDto getUserById(Long id) {
        return userRepository.findById(id).map(UserDto::new)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(UserDto::new)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getDisplayName(),
            request.getRole()
        );
        user.setEnabled(request.isEnabled());
        return new UserDto(userRepository.save(user));
    }

    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.ADMIN && request.getRole() != Role.ADMIN
                && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new RuntimeException("Cannot demote the last admin account");
        }
        user.setDisplayName(request.getDisplayName());
        user.setRole(request.getRole());
        user.setEnabled(request.isEnabled());
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return new UserDto(userRepository.save(user));
    }

    public UserDto updateSettings(String username, Map<String, Object> settings) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (settings.containsKey("useBatLauncher")) {
            user.setUseBatLauncher(Boolean.TRUE.equals(settings.get("useBatLauncher")));
        }
        return new UserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.ADMIN && userRepository.countByRole(Role.ADMIN) <= 1) {
            throw new RuntimeException("Cannot delete the last admin account");
        }
        assignmentRepository.findByUserId(id)
            .forEach(a -> assignmentRepository.delete(a));
        userRepository.deleteById(id);
    }
}
