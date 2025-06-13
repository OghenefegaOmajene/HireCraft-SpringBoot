package HireCraft.com.SpringBoot.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_provider_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String occupation;

    private String hourlyRate;

    @Column(length = 1000)
    private String bio;

    private String cvUrl;

    private double averageRating;

    @ElementCollection
    @CollectionTable(name = "provider_skills", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "providerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User user;

}
