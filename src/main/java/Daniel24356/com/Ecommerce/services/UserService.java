package Daniel24356.com.Ecommerce.services;

import Daniel24356.com.Ecommerce.dtos.requests.UserUpdateRequest;
import Daniel24356.com.Ecommerce.dtos.response.UserDetailResponse;
import Daniel24356.com.Ecommerce.dtos.response.UserListResponse;

import java.security.Principal;
import java.util.List;

public interface UserService {

    List<UserListResponse> getAllUsers();
    UserDetailResponse getUserById(Long id);
    UserDetailResponse updateUser(Long id, UserUpdateRequest request);
    Long getUserIdFromPrincipal(Principal principal);
    void deleteUser(Long id);
}
