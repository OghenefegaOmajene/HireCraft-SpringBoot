package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateServiceProviderProfileRequest {
    private String bio;
    private String cvUrl;
    private Set<String> skills;
}
