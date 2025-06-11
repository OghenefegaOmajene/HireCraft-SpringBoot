package HireCraft.com.SpringBoot.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {

    private Long id;
    private String clientFullName;
    private String clientCompany;
    private String clientPosition;

    private String city;
    private String state;
    private String country;

    private String timeSlot;
    private String estimatedDuration;
    private String description;
    private String timeAgo;
    private String status;
}
