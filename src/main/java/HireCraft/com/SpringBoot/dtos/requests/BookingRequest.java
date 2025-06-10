package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingRequest {
    private Long providerId;
    private String timeSlot;
    private String estimatedDuration;
    private String description;
}

