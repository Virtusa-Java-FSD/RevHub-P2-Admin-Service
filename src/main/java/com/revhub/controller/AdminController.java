package com.revhub.controller;

import com.revhub.dto.MessageResponse;
import com.revhub.dto.ResolveReportRequest;
import com.revhub.model.Report;
import com.revhub.model.Admin;
import com.revhub.service.ReportService;
import com.revhub.service.UserService;
import com.revhub.service.BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.revhub.repository.AdminRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
// @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for testing
public class AdminController {
    private final ReportService reportService;
    private final UserService userService;
    private final BucketService bucketService;
    private final AdminRepository adminRepository;

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/pending")
    public ResponseEntity<List<Report>> getPendingReports() {
        List<Report> reports = reportService.getReportsByStatus(Report.ReportStatus.PENDING);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable String id) {
        Report report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    @PutMapping("/reports/{id}/resolve")
    public ResponseEntity<Report> resolveReport(
            @PathVariable String id,
            @RequestBody ResolveReportRequest request,
            Authentication authentication) {
        Report report = reportService.resolveReport(id, authentication, request.getResolutionNote());
        return ResponseEntity.ok(report);
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<MessageResponse> blockUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String reason) {
        userService.blockUser(userId, reason);
        return ResponseEntity.ok(new MessageResponse("User blocked successfully"));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<MessageResponse> deleteComment(@PathVariable Long commentId) {
        // TODO: Implement comment deletion when CommentService is available
        return ResponseEntity.ok(new MessageResponse("Comment deletion not yet implemented"));
    }

    @GetMapping("/profile")
    public ResponseEntity<Admin> getAdminProfile(Authentication authentication) {
        System.out.println("=== getAdminProfile Request ===");
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + authentication.getPrincipal());
        String adminEmail = authentication.getName();
        System.out.println("Admin Email from Auth: " + adminEmail);

        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> {
                    System.err.println("Admin not found in DB for email: " + adminEmail);
                    return new RuntimeException("Admin not found");
                });
        System.out.println("Admin found: " + admin.getId() + ", " + admin.getEmail());
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateAdminProfile(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
            Authentication authentication) {
        try {
            System.out.println("=== Admin Profile Update Started ===");
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);
            System.out.println("Email: " + email);
            System.out.println(
                    "Profile Picture: " + (profilePicture != null ? profilePicture.getOriginalFilename() : "None"));
            // Get admin email from authentication
            String adminEmail = authentication.getName();
            System.out.println("Admin Email from Auth: " + adminEmail);
            Admin admin = adminRepository.findByEmail(adminEmail)
                    .orElseThrow(() -> new RuntimeException("Admin not found with email: " + adminEmail));
            System.out.println("Admin found: " + admin.getName());
            // Update name if firstName or lastName provided
            if ((firstName != null && !firstName.trim().isEmpty()) ||
                    (lastName != null && !lastName.trim().isEmpty())) {
                String newFirstName = (firstName != null && !firstName.trim().isEmpty()) ? firstName.trim() : "";
                String newLastName = (lastName != null && !lastName.trim().isEmpty()) ? lastName.trim() : "";
                if (!newFirstName.isEmpty() && !newLastName.isEmpty()) {
                    admin.setName(newFirstName + " " + newLastName);
                    System.out.println("Updated name to: " + admin.getName());
                } else if (!newFirstName.isEmpty()) {
                    admin.setName(newFirstName);
                    System.out.println("Updated name to: " + admin.getName());
                } else if (!newLastName.isEmpty()) {
                    admin.setName(newLastName);
                    System.out.println("Updated name to: " + admin.getName());
                }
            }
            // Update email if provided
            if (email != null && !email.trim().isEmpty()) {
                admin.setEmail(email.trim());
                System.out.println("Updated email to: " + admin.getEmail());
            }
            // Upload profile picture to S3 if provided
            if (profilePicture != null && !profilePicture.isEmpty()) {
                try {
                    System.out.println("Starting S3 upload...");
                    String bucketName = "revhub-suhas-bucket";
                    String profilePictureUrl = bucketService.uploadFile(profilePicture, bucketName);
                    admin.setProfilePicture(profilePictureUrl);
                    System.out.println("Profile picture uploaded: " + profilePictureUrl);
                } catch (Exception e) {
                    System.err.println("S3 upload failed: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(500)
                            .body(new MessageResponse("Failed to upload profile picture: " + e.getMessage()));
                }
            }
            // Save updated admin
            System.out.println("Saving admin to database...");
            Admin updatedAdmin = adminRepository.save(admin);
            System.out.println("Admin saved successfully!");
            System.out.println("=== Admin Profile Update Completed ===");
            return ResponseEntity.ok(updatedAdmin);
        } catch (Exception e) {
            System.err.println("=== Admin Profile Update Failed ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(new MessageResponse("Failed to update profile: " + e.getMessage()));
        }
    }

    @GetMapping("/debug/users")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Admin>> getAllAdminsDebug() {
        return ResponseEntity.ok(adminRepository.findAll());
    }
}
