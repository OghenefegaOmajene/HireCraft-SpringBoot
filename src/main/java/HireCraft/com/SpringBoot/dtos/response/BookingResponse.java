package HireCraft.com.SpringBoot.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {

    private Long id;
    private String profilePictureUrl;
    private String clientFullName;
    private String clientJobTitle;

    private String city;
    private String state;
    private String country;

    private String timeSlot;
    private String estimatedDuration;
    private String description;
    private String timeAgo;
    private String status;

    public void setId(Long id) {
        this.id = id;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public void setClientJobTitle(String clientJobTitle) {
        this.clientJobTitle = clientJobTitle;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
