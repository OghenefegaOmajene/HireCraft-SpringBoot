package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

import java.util.Set;

@Data
public class UnifiedUserProfileUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String country;
    private String state;
    private String city;

    // Client-specific fields
    private String companyName;
    private String position;
    private String profession;
    private String companyWebsiteUrl;
    private String clientBio;

    // Provider-specific fields
    private String occupation;
    private String providerBio;
    private String cvUrl;
    private Set<String> skills;
}
