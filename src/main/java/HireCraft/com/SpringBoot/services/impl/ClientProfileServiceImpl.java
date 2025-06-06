package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.UpdateClientProfileRequest;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.services.ClientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientProfileServiceImpl implements ClientProfileService {

    private final ClientProfileRepository clientProfileRepository;

    @Override
    public ClientProfile updateProfile(Long userId, UpdateClientProfileRequest request) {
        ClientProfile profile = clientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getCompanyWebsiteUrl() != null) {
            profile.setCompanyWebsiteUrl(request.getCompanyWebsiteUrl());
        }

        return clientProfileRepository.save(profile);
    }
}
