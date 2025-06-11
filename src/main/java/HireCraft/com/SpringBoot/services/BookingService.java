package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, UserDetails userDetails);
    List<BookingResponse> getBookingsForProvider(UserDetails userDetails);
    void updateStatus(Long bookingId, String status);
    List<ClientBookingViewResponse> getBookingsForClient(UserDetails userDetails);

}
