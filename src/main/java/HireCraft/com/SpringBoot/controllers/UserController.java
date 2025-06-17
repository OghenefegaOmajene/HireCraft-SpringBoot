package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import HireCraft.com.SpringBoot.exceptions.InvalidCvFileException;
import HireCraft.com.SpringBoot.exceptions.UserNotFoundException;
import HireCraft.com.SpringBoot.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
//@RequiredArgsConstructor
public class UserController {
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping("/getAll")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<UserListResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
//    @PreAuthorize("hasAuthority('VIEW_USER_PROFILE')")               // ①
    public ResponseEntity<UserDetailResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserDetailResponse profile = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/view-profile")
    @PreAuthorize("hasAuthority('VIEW_USER_PROFILE')")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
        UserDetailResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER_ACCOUNT') or hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-profile")
//    @PreAuthorize("hasAuthority('EDIT_USER_PROFILE')")
    public ResponseEntity<UserDetailResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UnifiedUserProfileUpdateRequest request) {
        UserDetailResponse updated = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/update-profile-picture")
    @PreAuthorize("hasAuthority('EDIT_USER_PROFILE')")
    public ResponseEntity<Map<String,String>> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails principal,
            @RequestPart("file") MultipartFile file) {

        String url = userService.updateProfilePicture(principal.getUsername(), file);
        return ResponseEntity.ok(Map.of("profilePictureUrl", url));
    }

    @PostMapping("/upload-cv") // Cleaner endpoint, no need for {email} in path
    public ResponseEntity<?> uploadCv(Principal principal, @RequestParam("file") MultipartFile file) {
        try {
            // The service layer will use the principal to find the user and upload the CV
            String cvUrl = userService.uploadCv(principal, file);
            return ResponseEntity.ok(cvUrl); // Return the URL of the uploaded CV
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidCvFileException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) { // Catch any other unexpected errors during upload
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload CV: " + e.getMessage());
        }
    }
}
