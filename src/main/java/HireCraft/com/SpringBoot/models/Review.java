package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Data
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double ratingNo;

    private String reviewTxt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientProfile clientProfile;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProviderProfile providerProfile;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
