package HireCraft.com.SpringBoot.services;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);
    List<BookingResponse> getBookingsForProvider(Long providerId);
}


