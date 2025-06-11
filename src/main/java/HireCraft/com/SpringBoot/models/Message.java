package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ClientProfile clientProfile;

    @ManyToOne
    private ServiceProviderProfile providerProfile;

    @ManyToOne
    private Booking booking; // Links conversation to the booking

    private String encryptedContent;

    @CreationTimestamp
    private LocalDateTime sentAt;
}
