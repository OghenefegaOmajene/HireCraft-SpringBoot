package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.ProfilePatchRequest;
import HireCraft.com.SpringBoot.dtos.requests.UnifiedUserProfileUpdateRequest;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.CloudinaryService;
import HireCraft.com.SpringBoot.services.UserService;
import HireCraft.com.SpringBoot.dtos.requests.UserUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import HireCraft.com.SpringBoot.models.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import HireCraft.com.SpringBoot.exceptions.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;

    @Override
    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserListResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getCity(),
                        user.getState(),
                        user.getCountry(),
                        user.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));
        return mapToDetail(user);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCountry(request.getCountry());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setUpdatedAt(java.time.LocalDateTime.now());

        User updated = userRepository.save(user);
        return mapToDetail(user);
    }

    @Override
    public Long getUserIdFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getId();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete, user not found with ID " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetailResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        return mapToDetail(user);
    }

    @Override
    public UserDetailResponse updateUserProfile(String email, UnifiedUserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // === Update base user fields ===
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getState() != null) user.setState(request.getState());
        if (request.getCity() != null) user.setCity(request.getCity());

        // === Client Profile Update ===
        if (user.getClientProfile() != null) {
            ClientProfile clientProfile = user.getClientProfile();
            if(request.getPosition() !=null) clientProfile.setPosition(request.getPosition());
            if(request.getProfession() !=null) clientProfile.setProfession(request.getProfession());
            if (request.getCompanyName() != null) clientProfile.setCompanyName(request.getCompanyName());
            if (request.getCompanyWebsiteUrl() != null) clientProfile.setCompanyWebsiteUrl(request.getCompanyWebsiteUrl());
            if (request.getClientBio() != null) clientProfile.setBio(request.getClientBio());
            clientProfileRepository.save(clientProfile);
        }

        // === Service Provider Profile Update ===
        if (user.getServiceProviderProfile() != null) {
            ServiceProviderProfile providerProfile = user.getServiceProviderProfile();
            if(request.getOccupation() !=null) providerProfile.setOccupation(request.getPosition());
            if (request.getProviderBio() != null) providerProfile.setBio(request.getProviderBio());
            if (request.getCvUrl() != null) providerProfile.setCvUrl(request.getCvUrl());
            if (request.getSkills() != null && !request.getSkills().isEmpty()) {
                providerProfile.setSkills(request.getSkills());
            }
            serviceProviderProfileRepository.save(providerProfile);
        }

        userRepository.save(user);

//        return userMapper.toUserDetailResponse(user);
        return mapToDetail(user);

    }


    @Override
    @Transactional
    public String updateProfilePicture(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        String url = cloudinaryService.uploadProfileImage(file);
        user.setProfilePictureUrl(url);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return url;
    }


    private UserDetailResponse mapToDetail(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getState(),
                user.getCity(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfilePictureUrl()
        );
    }
}
