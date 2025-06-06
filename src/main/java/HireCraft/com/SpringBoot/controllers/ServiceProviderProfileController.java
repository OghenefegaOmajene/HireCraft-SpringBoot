package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.UpdateServiceProviderProfileRequest;
import HireCraft.com.SpringBoot.services.ServiceProviderProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers/profile")
@RequiredArgsConstructor
public class ServiceProviderProfileController {

    private final ServiceProviderProfileService serviceProviderProfileService;

    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateServiceProviderProfileRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        serviceProviderProfileService.updateProfile(request, userDetails.getUsername());
        return ResponseEntity.ok("Profile updated successfully");
    }
}

