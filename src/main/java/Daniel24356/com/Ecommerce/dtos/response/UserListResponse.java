package Daniel24356.com.Ecommerce.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
}

