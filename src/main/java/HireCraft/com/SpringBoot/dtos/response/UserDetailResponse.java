package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String state;
    private String city;
    private String status;

    private double averageRating;
    private String occupation;
    private String hourlyRate;
    private String providerBio;
    private Set<String> skills;
    private String cvUrl;

    private String companyName;
    private String position;
    private String profession;
    private String companyWebsiteUrl;
    private String clientBio;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profilePictureUrl;

}

