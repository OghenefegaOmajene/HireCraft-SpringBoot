package HireCraft.com.SpringBoot.services;

public interface MessageService {
    void sendMessageToBooking(MessageRequest request, UserDetails userDetails);
    List<MessageResponse> getConversation(Long bookingId);
}

