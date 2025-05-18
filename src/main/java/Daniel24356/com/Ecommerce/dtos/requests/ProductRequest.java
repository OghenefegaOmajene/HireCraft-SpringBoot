package Daniel24356.com.Ecommerce.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    private String contactInfo;
    private LocalDateTime postedAt;
    private String sellerUsername;
}
