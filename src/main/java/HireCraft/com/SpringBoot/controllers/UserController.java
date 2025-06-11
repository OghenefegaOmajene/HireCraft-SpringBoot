package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.ProfilePatchRequest;
import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.dtos.requests.UserUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import HireCraft.com.SpringBoot.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * List all users. Only admins with MANAGE_USERS permission can access.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<UserListResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user details by ID. Requires VIEW_USER_PROFILE permission.
     * @param id user identifier
     * @return detailed user information
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_USER_PROFILE')")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable Long id) {
        UserDetailResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update an existing user's profile. Requires EDIT_USER_PROFILE permission.
     * @param id user identifier
     * @param request payload containing updated fields
     * @return updated user information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_USER_PROFILE')")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDetailResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a user account. Requires DELETE_USER_ACCOUNT permission.
     * @param id user identifier
     * @return no content on successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER_ACCOUNT')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")               // ①
    public ResponseEntity<UserDetailResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserDetailResponse profile = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }


    @PatchMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDetailResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UnifiedUserProfileUpdateRequest request) {
        UserDetailResponse updated = userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/profile-picture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String,String>> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails principal,
            @RequestPart("file") MultipartFile file) {

        String url = userService.updateProfilePicture(principal.getUsername(), file);
        return ResponseEntity.ok(Map.of("profilePictureUrl", url));
    }
}
