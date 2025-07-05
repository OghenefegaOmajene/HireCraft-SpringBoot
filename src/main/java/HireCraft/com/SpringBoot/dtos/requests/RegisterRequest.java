package HireCraft.com.SpringBoot.dtos.requests;

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
public class RegisterRequest {

    @NotBlank(message = "First name must not be empty")
    private String firstName;

    @NotBlank(message = "Last name must not be empty")
    private String lastName;

    @Email(message = "Must be a valid email address")
    @NotBlank(message = "Email must not be empty")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Enter your country")
    private String country;

    @NotBlank(message = "Enter your state")
    private String state;

    @NotBlank(message = "Enter your city")
    private String city;

    private String phoneNumber;

    private String occupation;

    private String hourlyRate;

    private String role;

    public String jobTitle;

    public @NotBlank(message = "First name must not be empty") String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(message = "First name must not be empty") String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(message = "Last name must not be empty") String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(message = "Last name must not be empty") String lastName) {
        this.lastName = lastName;
    }

    public @Email(message = "Must be a valid email address") @NotBlank(message = "Email must not be empty") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Must be a valid email address") @NotBlank(message = "Email must not be empty") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password must not be empty") @Size(min = 4, message = "Password must be at least 4 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password must not be empty") @Size(min = 4, message = "Password must be at least 4 characters") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Enter your country") String getCountry() {
        return country;
    }

    public void setCountry(@NotBlank(message = "Enter your country") String country) {
        this.country = country;
    }

    public @NotBlank(message = "Enter your state") String getState() {
        return state;
    }

    public void setState(@NotBlank(message = "Enter your state") String state) {
        this.state = state;
    }

    public @NotBlank(message = "Enter your city") String getCity() {
        return city;
    }

    public void setCity(@NotBlank(message = "Enter your city") String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
