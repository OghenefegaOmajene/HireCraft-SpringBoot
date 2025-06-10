package HireCraft.com.SpringBoot.dtos.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String senderType; // "CLIENT" or "PROVIDER"
    private String content; // decrypted
    private String sentAt; // e.g., "Yesterday", "Today", "3:45 PM"
}

