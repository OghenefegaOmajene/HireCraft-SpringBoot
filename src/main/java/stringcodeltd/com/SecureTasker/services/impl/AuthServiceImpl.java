package stringcodeltd.com.SecureTasker.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stringcodeltd.com.SecureTasker.dtos.requests.LoginRequest;
import stringcodeltd.com.SecureTasker.dtos.requests.RegisterRequest;
import stringcodeltd.com.SecureTasker.dtos.response.LoginResponse;
import stringcodeltd.com.SecureTasker.dtos.response.RegisterResponse;
import stringcodeltd.com.SecureTasker.enums.UserStatus;
import stringcodeltd.com.SecureTasker.exceptions.UserAlreadyExistsException;
import stringcodeltd.com.SecureTasker.models.User;
import stringcodeltd.com.SecureTasker.models.Role;
import stringcodeltd.com.SecureTasker.repository.RoleRepository;
import stringcodeltd.com.SecureTasker.repository.UserRepository;
import stringcodeltd.com.SecureTasker.security.jwt.JwtTokenProvider;
import stringcodeltd.com.SecureTasker.services.AuthService;
import stringcodeltd.com.SecureTasker.utils.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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

}
