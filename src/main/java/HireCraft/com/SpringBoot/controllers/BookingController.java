package HireCraft.com.SpringBoot.controllers;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.requests.UpdateBookingStatusRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
import HireCraft.com.SpringBoot.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
//@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('BOOK_SERVICE_PROVIDER')")
    public BookingResponse createBooking(@RequestBody BookingRequest request,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.createBooking(request, userDetails);
    }

    @GetMapping("/provider/me")
    @PreAuthorize("hasAuthority('VIEW_BOOKING_REQUEST_PROVIDER')")
    public List<BookingResponse> getProviderBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getBookingsForProvider(userDetails);
    }

    @GetMapping("/client/me")
    @PreAuthorize("hasAuthority('VIEW_BOOKING_REQUEST_CLIENT')")
    public List<ClientBookingViewResponse> getClientBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getBookingsForClient(userDetails);
    }


    @PatchMapping("/{bookingId}/status") // Use PATCH for partial update
    @PreAuthorize("hasAuthority('CANCEL_BOOKING_REQUEST') or hasAuthority('UPDATE_BOOKING_REQUEST_STATUS')")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long bookingId,
                                                               @RequestBody @Valid UpdateBookingStatusRequest request,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        BookingResponse updatedBooking = bookingService.updateBookingStatus(bookingId, request, userDetails);
        return ResponseEntity.ok(updatedBooking);
    }

    @GetMapping("/provider/dashboard/new-requests-today")
    @PreAuthorize("hasAuthority('UPDATE_BOOKING_REQUEST_STATUS')")
    public ResponseEntity<Long> getNewBookingRequestsToday(@AuthenticationPrincipal UserDetails userDetails) {
        long count = bookingService.getNewBookingRequestsCountToday(userDetails);
        return ResponseEntity.ok(count);
    }

    // New endpoint for completed jobs count
    @GetMapping("/provider/dashboard/completed-jobs")
    @PreAuthorize("hasAuthority('UPDATE_BOOKING_REQUEST_STATUS')")
    public ResponseEntity<Long> getCompletedJobsCount(@AuthenticationPrincipal UserDetails userDetails) {
        long count = bookingService.getCompletedJobsCountForProvider(userDetails);
        return ResponseEntity.ok(count);
    }
}
