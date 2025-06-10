package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public BookingResponse createBooking(@RequestBody BookingRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.createBooking(request, userDetails);
    }

    @GetMapping("/provider/{providerId}")
    public List<BookingResponse> getProviderBookings(@PathVariable Long providerId) {
        return bookingService.getBookingsForProvider(providerId);
    }

    @PutMapping("/{bookingId}/status")
    public void updateStatus(@PathVariable Long bookingId,
                             @RequestParam String status) {
        bookingService.updateStatus(bookingId, status);
    }
}
