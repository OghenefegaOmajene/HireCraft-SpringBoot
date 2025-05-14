package stringcodeltd.com.SecureTasker.services;

import stringcodeltd.com.SecureTasker.dtos.requests.UserUpdateRequest;
import stringcodeltd.com.SecureTasker.dtos.response.UserDetailResponse;
import stringcodeltd.com.SecureTasker.dtos.response.UserListResponse;

import java.security.Principal;
import java.util.List;

public interface UserService {

    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserById(Long id);
    UserDetailResponse updateUser(Long id, UserUpdateRequest request);
    Long getUserIdFromPrincipal(Principal principal);
    void deleteUser(Long id);
}
