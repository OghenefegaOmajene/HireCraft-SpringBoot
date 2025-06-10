package HireCraft.com.SpringBoot.dtos.requests;

import lombok.Data;

@Data
public class MessageRequest {
    private Long bookingId;
    private String content; // Raw content (to be encrypted)
}
