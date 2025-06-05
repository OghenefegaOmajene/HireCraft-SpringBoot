package HireCraft.com.SpringBoot.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating user profile fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "First name must not be blank")
    private String email;

    @NotBlank(message = "Last name must not be blank")
    private String phoneNumber;

    @NotBlank(message = "First name must not be blank")
    private String country;

    @NotBlank(message = "Last name must not be blank")
    private String state;

    @NotBlank(message = "First name must not be blank")
    private String city;

}

