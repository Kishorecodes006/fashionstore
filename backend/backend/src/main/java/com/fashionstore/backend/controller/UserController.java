package com.fashionstore.backend.controller;

import com.fashionstore.backend.model.User;
import com.fashionstore.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET single user by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered!"));
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    // POST login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email or password!"));
        }

        User user = userOpt.get();
        if (user.isBlocked()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Your account has been blocked. Please contact support."));
        }

        return ResponseEntity.ok(user);
    }

    // PUT update profile (name, email, phone, address, password)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updated) {
        return userRepository.findById(id).map(user -> {

            // Check if new email is taken by someone else
            if (updated.getEmail() != null && !updated.getEmail().equals(user.getEmail())
                    && userRepository.existsByEmail(updated.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already in use by another account!"));
            }

            if (updated.getName() != null)    user.setName(updated.getName());
            if (updated.getEmail() != null)   user.setEmail(updated.getEmail());
            if (updated.getPhone() != null)   user.setPhone(updated.getPhone());
            if (updated.getAddress() != null) user.setAddress(updated.getAddress());

            if (updated.getPassword() != null && !updated.getPassword().isEmpty()) {
                user.setPassword(updated.getPassword());
            }

            return (ResponseEntity<?>) ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PUT block/unblock
    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable Long id,
            @RequestParam boolean blocked) {
        return userRepository.findById(id).map(user -> {
            user.setBlocked(blocked);
            return ResponseEntity.ok(userRepository.save(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted!"));
    }
}