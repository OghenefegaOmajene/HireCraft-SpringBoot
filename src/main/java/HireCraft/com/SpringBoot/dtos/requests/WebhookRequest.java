package HireCraft.com.SpringBoot.dtos.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequest {
    private String event;
    private Object data;
}
