package com.revhub.controller;

import com.revhub.dto.AdminPasswordResetRequest;
import com.revhub.dto.MessageResponse;
import com.revhub.dto.VerifyOtpRequest;
import com.revhub.model.Admin;
import com.revhub.model.UserRole;
import com.revhub.repository.AdminRepository;
import com.revhub.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:*", allowedHeaders = "*", methods = {
        RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS }, allowCredentials = "true")
public class AdminPasswordResetController {
    private final AdminRepository adminRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    // Store OTPs temporarily (in production, use Redis or database)
    private final Map<String, OtpData> otpStore = new HashMap<>();

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> sendOtp(@RequestBody AdminPasswordResetRequest request) {
        // Find admin by email
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No admin account found with this email"));
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        // Store OTP with expiration (10 minutes)
        otpStore.put(request.getEmail(), new OtpData(otp, LocalDateTime.now().plusMinutes(10)));
        // Send OTP via email
        emailService.sendOtpEmail(request.getEmail(), otp);
        return ResponseEntity.ok(new MessageResponse("OTP has been sent to your email"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<MessageResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        // Check if OTP exists
        OtpData otpData = otpStore.get(request.getEmail());
        if (otpData == null) {
            throw new RuntimeException("No OTP found for this email. Please request a new one.");
        }
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            otpStore.remove(request.getEmail());
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }
        // Verify OTP
        if (!otpData.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }
        // Find admin
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        if (admin.getRole() != UserRole.ADMIN) {
            // Should not happen if table only has admins, but double check
            throw new RuntimeException("This email is not associated with an admin account");
        }
        // OTP is valid - don't remove it yet, will be removed after password reset
        return ResponseEntity.ok(new MessageResponse("OTP verified successfully"));
    }

    @PostMapping("/verify-otp-reset-password")
    public ResponseEntity<MessageResponse> verifyOtpAndResetPassword(@RequestBody VerifyOtpRequest request) {
        // Check if OTP exists
        OtpData otpData = otpStore.get(request.getEmail());
        if (otpData == null) {
            throw new RuntimeException("No OTP found for this email. Please request a new one.");
        }
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            otpStore.remove(request.getEmail());
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }
        // Verify OTP
        if (!otpData.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP. Please try again.");
        }
        // Find admin
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        // Update password
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminRepository.save(admin);
        // Remove OTP after successful reset
        otpStore.remove(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Password reset successful"));
    }

    // Inner class to store OTP data
    private static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
