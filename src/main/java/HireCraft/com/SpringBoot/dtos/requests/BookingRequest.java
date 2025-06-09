package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingRequest {
    private Long clientId;
    private Long providerId;
    private LocalDate bookingDate;
    private String timeSlot;
    private String location;
    private String description;
}
