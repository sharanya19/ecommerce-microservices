package com.ecommerce.user.service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.UserDTO;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.util.JwtUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public UserService(UserRepository userRepository, KafkaTemplate<String, Object> kafkaTemplate,
                      PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), "Login successful");
    }
    
    @Cacheable(value = "users", key = "#id")
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return UserDTO.fromEntity(user);
    }
    
    @Cacheable(value = "users", key = "#username")
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
        return UserDTO.fromEntity(user);
    }
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Publish user created event
        kafkaTemplate.send("user-events", "user.created", savedUser);
        
        return UserDTO.fromEntity(savedUser);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        
        user.setEmail(userDetails.getEmail());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        
        User updatedUser = userRepository.save(user);
        
        // Publish user updated event
        kafkaTemplate.send("user-events", "user.updated", updatedUser);
        
        return UserDTO.fromEntity(updatedUser);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
        
        // Publish user deleted event
        kafkaTemplate.send("user-events", "user.deleted", id);
    }
}

