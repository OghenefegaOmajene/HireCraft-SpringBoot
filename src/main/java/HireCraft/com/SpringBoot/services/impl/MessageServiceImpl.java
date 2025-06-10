package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import HireCraft.com.SpringBoot.models.Message;
import HireCraft.com.SpringBoot.repository.BookingRepository;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.MessageRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ClientProfileRepository clientRepo;
    private final ServiceProviderProfileRepository providerRepo;
    private final BookingRepository bookingRepo;
    private final EncryptionService encryptionService; // for E2EE

    @Override
    public void sendMessageToBooking(MessageRequest request, UserDetails userDetails) {
        Booking booking = bookingRepo.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String encrypted = encryptionService.encrypt(request.getContent());

        Message message = new Message();
        message.setEncryptedContent(encrypted);
        message.setBooking(booking);

        String email = userDetails.getUsername();

        clientRepo.findByUserEmail(email).ifPresentOrElse(client -> {
            message.setSenderClient(client);
        }, () -> {
            providerRepo.findByUserEmail(email).ifPresentOrElse(provider -> {
                message.setSenderProvider(provider);
            }, () -> {
                throw new RuntimeException("Sender not found");
            });
        });

        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getConversation(Long bookingId) {
        return messageRepository.findByBookingIdOrderBySentAtAsc(bookingId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setContent(encryptionService.decrypt(message.getEncryptedContent()));
        response.setSenderType(message.getSenderClient() != null ? "CLIENT" : "PROVIDER");
        response.setSentAt(formatTime(message.getSentAt()));
        return response;
    }

    private String formatTime(LocalDateTime sentAt) {
        LocalDate today = LocalDate.now();
        LocalDate msgDate = sentAt.toLocalDate();

        if (msgDate.isEqual(today)) return sentAt.toLocalTime().toString();
        else if (msgDate.plusDays(1).isEqual(today)) return "Yesterday";
        else return msgDate.toString();
    }
}
