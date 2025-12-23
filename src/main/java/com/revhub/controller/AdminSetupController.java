package com.revhub.controller;
import com.revhub.dto.MessageResponse;
import com.revhub.model.Admin;
import com.revhub.model.UserRole;
import com.revhub.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:*", allowedHeaders = "*", maxAge = 3600)
public class AdminSetupController {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/create-admin")
    public ResponseEntity<MessageResponse> createAdmin(@RequestParam String email, @RequestParam String name,
            @RequestParam String password) {
        if (adminRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Admin with this email already exists"));
        }
        Admin admin = Admin.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ADMIN)
                .build();
        adminRepository.save(admin);
        return ResponseEntity.ok(new MessageResponse("Admin " + email + " created successfully"));
    }
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetAdminPassword(@RequestParam String email,
            @RequestParam String newPassword) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        return ResponseEntity.ok(new MessageResponse("Admin password reset successfully"));
    }
}
