package HireCraft.com.SpringBoot.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordResponse {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }
}
