package HireCraft.com.SpringBoot.dtos.response;

import lombok.Data;

@Data
public class MessageResponse {
    private String senderType;         // "CLIENT" or "PROVIDER"
    private String senderFullName;     // From either ClientProfile or ServiceProviderProfile
    private String content;            // Decrypted message content
    private String dateStamp;          // e.g., "Today", "Yesterday"
    private String timeSent;           // e.g., "3:45 PM"
}
