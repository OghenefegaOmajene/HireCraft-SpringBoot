package Daniel24356.com.Ecommerce.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sellerUsername;
    private String title;
    private String description;
    private Double price;

    private String imageUrl;
    private String contactInfo; // email or phone of seller

    @ManyToOne
    private User seller;

    private LocalDateTime postedAt;


}

