package stringcodeltd.com.SecureTasker.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;

    private String imageUrl;
    private String contactInfo; // email or phone of seller

    @ManyToOne
    private User seller;

    private LocalDateTime postedAt;
}

