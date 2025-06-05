package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.ProfilePatchRequest;
import HireCraft.com.SpringBoot.dtos.requests.UserUpdateRequest;
import HireCraft.com.SpringBoot.dtos.response.UserDetailResponse;
import HireCraft.com.SpringBoot.dtos.response.UserListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface UserService {

    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserById(Long id);
    UserDetailResponse updateUser(Long id, UserUpdateRequest request);
    Long getUserIdFromPrincipal(Principal principal);
    void deleteUser(Long id);
    UserDetailResponse getUserByEmail(String email);
    UserDetailResponse updateUserProfile(String email, ProfilePatchRequest request);
    String updateProfilePicture(String email, MultipartFile file);
}
