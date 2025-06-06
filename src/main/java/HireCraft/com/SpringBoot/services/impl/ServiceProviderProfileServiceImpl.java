package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.UpdateServiceProviderProfileRequest;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.ServiceProviderProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceProviderProfileServiceImpl implements ServiceProviderProfileService {

    private final ServiceProviderProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Override
    public void updateProfile(UpdateServiceProviderProfileRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        ServiceProviderProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Profile not found"));

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getCvUrl() != null) profile.setCvUrl(request.getCvUrl());
        if (request.getSkills() != null) profile.setSkills(request.getSkills());

        profileRepository.save(profile);
    }
}
