package Daniel24356.com.Ecommerce.services;

import Daniel24356.com.Ecommerce.dtos.requests.ForgetPasswordRequest;
import Daniel24356.com.Ecommerce.dtos.requests.RegisterRequest;
import Daniel24356.com.Ecommerce.dtos.requests.LoginRequest;
import Daniel24356.com.Ecommerce.dtos.requests.ResetPasswordRequest;
import Daniel24356.com.Ecommerce.dtos.response.ForgotPasswordResponse;
import Daniel24356.com.Ecommerce.dtos.response.LoginResponse;
import Daniel24356.com.Ecommerce.dtos.response.RegisterResponse;
import Daniel24356.com.Ecommerce.dtos.response.ResetPasswordResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(ForgetPasswordRequest request);

    /**
     * Complete a password reset by validating the code
     * and setting the new password.
     */
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
