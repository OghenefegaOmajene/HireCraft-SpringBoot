package HireCraft.com.SpringBoot.models;

import HireCraft.com.SpringBoot.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ClientProfile clientProfile;

    @ManyToOne
    private ServiceProviderProfile providerProfile;

    private String timeSlot;
    private String location;
    private String description;
    private String estimatedDuration;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

