package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.enums.BookingStatus;
import HireCraft.com.SpringBoot.models.Booking;
import HireCraft.com.SpringBoot.models.ClientProfile;
import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
import HireCraft.com.SpringBoot.models.User;
import HireCraft.com.SpringBoot.repository.BookingRepository;
import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
import HireCraft.com.SpringBoot.repository.UserRepository;
import HireCraft.com.SpringBoot.services.BookingService;
import HireCraft.com.SpringBoot.utils.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository providerProfileRepository;
    private final TimeAgoUtil timeAgoUtil;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        ClientProfile clientProfile = clientProfileRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ServiceProviderProfile providerProfile = providerProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Booking booking = new Booking();
        booking.setClientProfile(clientProfile);
        booking.setProviderProfile(providerProfile);
        booking.setTimeSlot(request.getTimeSlot());
        booking.setLocation(request.getLocation());
        booking.setDescription(request.getDescription());
        booking.setEstimatedDuration(request.getEstimatedDuration());
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsForProvider(Long providerId) {
        return bookingRepository.findById(providerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToResponse(Booking booking) {
        User clientUser = booking.getClientProfile().getUser();

        String fullName = clientUser.getFirstName() + " " + clientUser.getLastName();
        String location = clientUser.getCity() + ", " + clientUser.getState() + ", " + clientUser.getCountry();
        String timeAgo = timeAgoUtil.format(booking.getCreatedAt());

        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setClientName(fullName);
        response.setProviderName(booking.getProviderProfile().getUser().getFirstName());
        response.setDescription(booking.getDescription());
        response.setLocation(location);
        response.setTimeSlot(booking.getTimeSlot());
        response.setEstimatedDuration(booking.getEstimatedDuration());
        response.setStatus(booking.getStatus().name());
        response.setTimeAgo(timeAgo);

        return response;
    }
}



