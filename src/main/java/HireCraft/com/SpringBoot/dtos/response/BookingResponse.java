package HireCraft.com.SpringBoot.dtos.response;

import HireCraft.com.SpringBoot.enums.BookingStatus;
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
    private String status;
    private String timeAgo;
}
