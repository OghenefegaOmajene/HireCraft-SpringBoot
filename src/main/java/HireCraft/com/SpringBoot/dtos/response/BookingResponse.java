package HireCraft.com.SpringBoot.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingResponse {
    private Long id;
    private String clientName;
    private String providerName;
    private String description;
    private String location;
    private String timeSlot;
    private String estimatedDuration;
    private String status;
    private String timeAgo;
}
