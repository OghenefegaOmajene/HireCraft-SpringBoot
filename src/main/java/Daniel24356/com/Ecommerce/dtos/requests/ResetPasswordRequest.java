package Daniel24356.com.Ecommerce.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank @Size(min = 6, max = 6)
    private String token;      // the 6-digit code

    @NotBlank @Size(min = 8)
    private String newPassword;
}
