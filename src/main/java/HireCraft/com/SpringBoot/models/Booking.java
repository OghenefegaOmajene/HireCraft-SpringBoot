package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AppUser client;

    @ManyToOne
    private AppUser provider;

    private LocalDate bookingDate;
    private String timeSlot;
    private String location;
    private String description;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
}
