package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import HireCraft.com.SpringBoot.models.Booking;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.Message;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.repository.BookingRepository;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.MessageRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.services.MessageService;
import HireCraft.com.SpringBoot.utils.TimeDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository providerRepository;

    @Override
    public MessageResponse sendMessage(MessageRequest request, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Message message = new Message();
        message.setBooking(booking);
        message.setEncryptedContent(request.getContent()); // optionally encrypt here

        ClientProfile client = clientProfileRepository.findByUserEmail(userDetails.getUsername()).orElse(null);
        ServiceProviderProfile provider = providerRepository.findByUserEmail(userDetails.getUsername()).orElse(null);

        if (client != null) {
            message.setSenderClient(client);
        } else if (provider != null) {
            message.setSenderProvider(provider);
        } else {
            throw new RuntimeException("Unauthorized sender");
        }

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    public List<MessageResponse> getMessagesForBooking(Long bookingId) {
        return messageRepository.findByBookingIdOrderBySentAtAsc(bookingId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private MessageResponse mapToResponse(Message message) {
        MessageResponse response = new MessageResponse();

        if (message.getSenderClient() != null) {
            response.setSenderType("CLIENT");
            response.setSenderFullName(message.getSenderClient().getUser().getFirstName()
                    + " " + message.getSenderClient().getUser().getLastName());
        } else {
            response.setSenderType("PROVIDER");
            response.setSenderFullName(message.getSenderProvider().getUser().getFirstName()
                    + " " + message.getSenderProvider().getUser().getLastName());
        }

        response.setContent(message.getEncryptedContent()); // optionally decrypt
        response.setDateStamp(TimeDateUtil.getDateLabel(message.getSentAt()));
        response.setTimeSent(TimeDateUtil.formatTime(message.getSentAt()));

        return response;
    }
}
