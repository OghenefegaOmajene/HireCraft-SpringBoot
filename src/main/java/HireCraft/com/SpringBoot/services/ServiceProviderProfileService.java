package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.UpdateServiceProviderProfileRequest;

public interface ServiceProviderProfileService {
    void updateProfile(UpdateServiceProviderProfileRequest request, String email);
}

