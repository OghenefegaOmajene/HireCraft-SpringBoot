package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.ForgetPasswordRequest;
import HireCraft.com.SpringBoot.dtos.requests.RegisterRequest;
import HireCraft.com.SpringBoot.dtos.requests.LoginRequest;
import HireCraft.com.SpringBoot.dtos.requests.ResetPasswordRequest;
import HireCraft.com.SpringBoot.dtos.response.ForgotPasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.LoginResponse;
import HireCraft.com.SpringBoot.dtos.response.RegisterResponse;
import HireCraft.com.SpringBoot.dtos.response.ResetPasswordResponse;

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
