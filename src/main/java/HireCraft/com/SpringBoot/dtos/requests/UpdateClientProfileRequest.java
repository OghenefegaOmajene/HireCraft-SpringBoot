package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

@Data
public class UpdateClientProfileRequest {
    private String bio;
    private String companyWebsiteUrl;
    private String companyName;
}

