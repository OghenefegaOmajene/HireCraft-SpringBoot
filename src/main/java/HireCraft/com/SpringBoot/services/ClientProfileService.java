package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.UpdateClientProfileRequest;
import HireCraft.com.SpringBoot.models.ClientProfile;

public interface ClientProfileService {
    ClientProfile updateProfile(Long userId, UpdateClientProfileRequest request);
}
