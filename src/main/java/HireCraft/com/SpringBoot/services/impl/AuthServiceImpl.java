package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.ForgetPasswordRequest;
import HireCraft.com.SpringBoot.dtos.requests.RegisterRequest;
import HireCraft.com.SpringBoot.dtos.requests.ResetPasswordRequest;
import HireCraft.com.SpringBoot.dtos.response.ForgotPasswordResponse;
import HireCraft.com.SpringBoot.dtos.response.ResetPasswordResponse;
import HireCraft.com.SpringBoot.exceptions.InvalidResetTokenException;
import HireCraft.com.SpringBoot.models.PasswordResetToken;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.AuthService;
import HireCraft.com.SpringBoot.dtos.requests.LoginRequest;
import HireCraft.com.SpringBoot.dtos.response.LoginResponse;
import HireCraft.com.SpringBoot.dtos.response.RegisterResponse;
import HireCraft.com.SpringBoot.enums.UserStatus;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.security.jwt.JwtTokenProvider;
import HireCraft.com.SpringBoot.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import HireCraft.com.SpringBoot.exceptions.UserAlreadyExistsException;
import HireCraft.com.SpringBoot.models.Role;
import HireCraft.com.SpringBoot.repository.RoleRepository;
import HireCraft.com.SpringBoot.repository.PasswordResetTokenRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenRepository tokenRepository;
    private static final int TOKEN_LENGTH = 6;
    private static final int TOKEN_EXPIRY_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final JavaMailSender mailSender;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        // 1. Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "A user with email '" + request.getEmail() + "' already exists."
            );
        }

        // 2. Fetch default role (ROLE_USER)
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));

        // 3. Build User entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                // encode password via util
                .passwordHash(PasswordUtil.encode(request.getPassword()))
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Collections.singleton(defaultRole))
                .build();

        // 4. Persist
        User saved = userRepository.save(user);

        // 5. Return response
        return RegisterResponse.builder()
                .userId(saved.getId())
                .message("Registration successful for user: " + saved.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getValidityInMilliseconds())
                .build();
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgetPasswordRequest request) {
        // 1. Lookup user (silent if not found)
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            // 2. Generate 6-digit code
            String code = String.format("%0" + TOKEN_LENGTH + "d", RANDOM.nextInt(1_000_000));

            // 3. Save token
            PasswordResetToken prt = PasswordResetToken.builder()
                    .user(user)
                    .token(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                    .used(false)
                    .build();
            tokenRepository.save(prt);

            // 4. Send email
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(user.getEmail());
            msg.setSubject("Your Password Reset Code");
            msg.setText("Your password reset code is: " + code
                    + "\nThis code will expire in " + TOKEN_EXPIRY_MINUTES + " minutes.");
            mailSender.send(msg);
        });

        // 5. Always return success message to prevent account enumeration
        return new ForgotPasswordResponse(
                "If that email is registered, you will receive a reset code shortly."
        );
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        // 1. Validate reset token
        PasswordResetToken prt = tokenRepository
                .findFirstByUserEmailAndTokenOrderByExpiresAtDesc(
                        request.getEmail(), request.getToken())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid reset code."));

        if (prt.isUsed() || prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Reset code expired or already used.");
        }

        // 2. Mark token used
        prt.setUsed(true);
        tokenRepository.save(prt);

        // 3. Update user password
        User user = prt.getUser();
        user.setPasswordHash(PasswordUtil.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // 4. Return success
        return new ResetPasswordResponse("Password has been reset successfully.");
    }

}
