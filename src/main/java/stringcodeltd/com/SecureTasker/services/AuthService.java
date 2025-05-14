package stringcodeltd.com.SecureTasker.services;

import stringcodeltd.com.SecureTasker.dtos.requests.LoginRequest;
import stringcodeltd.com.SecureTasker.dtos.requests.RegisterRequest;
import stringcodeltd.com.SecureTasker.dtos.response.LoginResponse;
import stringcodeltd.com.SecureTasker.dtos.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
