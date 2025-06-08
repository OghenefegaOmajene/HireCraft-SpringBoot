package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long providerId;
    private LocalDate bookingDate;
    private String timeSlot;
    private String location;
    private String description;
}

