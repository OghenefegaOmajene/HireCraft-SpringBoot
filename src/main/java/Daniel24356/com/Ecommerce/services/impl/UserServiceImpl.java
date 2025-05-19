package Daniel24356.com.Ecommerce.services.impl;

import Daniel24356.com.Ecommerce.dtos.requests.ProfilePatchRequest;
import Daniel24356.com.Ecommerce.repository.UserRepository;
import Daniel24356.com.Ecommerce.services.CloudinaryService;
import Daniel24356.com.Ecommerce.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import Daniel24356.com.Ecommerce.dtos.requests.UserUpdateRequest;
import Daniel24356.com.Ecommerce.dtos.response.UserDetailResponse;
import Daniel24356.com.Ecommerce.dtos.response.UserListResponse;
import Daniel24356.com.Ecommerce.exceptions.UserNotFoundException;
import Daniel24356.com.Ecommerce.models.User;
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

    @Override
    public List<UserListResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserListResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
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
        // optionally update email or other fields
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
    public UserDetailResponse updateUserProfile(String email, ProfilePatchRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

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
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getProfilePictureUrl()
        );
    }
}
