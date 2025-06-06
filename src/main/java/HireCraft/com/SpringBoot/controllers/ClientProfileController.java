package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.UpdateClientProfileRequest;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.services.ClientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/profile")
@RequiredArgsConstructor
public class ClientProfileController {

    private final ClientProfileService clientProfileService;

    @PutMapping("/update")
    public ResponseEntity<ClientProfile> updateProfile(
            @RequestParam Long userId,
            @RequestBody UpdateClientProfileRequest request
    ) {
        ClientProfile updated = clientProfileService.updateProfile(userId, request);
        return ResponseEntity.ok(updated);
    }
}
