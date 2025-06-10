package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.MessageRequest;
import HireCraft.com.SpringBoot.dtos.response.MessageResponse;
import HireCraft.com.SpringBoot.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/chat/send")
    @SendTo("/topic/messages/{bookingId}")
    public MessageResponse sendMessage(@Payload MessageRequest request,
                                       @Header("simpUser") Principal principal) {
        UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
        messageService.sendMessageToBooking(request, userDetails);
        return messageService.getConversation(request.getBookingId())
                .stream()
                .reduce((first, second) -> second).orElse(null); // Return latest
    }
}

