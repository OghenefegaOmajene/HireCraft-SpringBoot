package Daniel24356.com.Ecommerce.services;

import Daniel24356.com.Ecommerce.dtos.requests.RegisterRequest;
import Daniel24356.com.Ecommerce.dtos.requests.LoginRequest;
import Daniel24356.com.Ecommerce.dtos.response.LoginResponse;
import Daniel24356.com.Ecommerce.dtos.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
