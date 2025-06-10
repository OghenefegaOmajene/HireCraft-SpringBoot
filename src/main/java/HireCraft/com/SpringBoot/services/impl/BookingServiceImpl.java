package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;



@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request, UserDetails userDetails) {
        ClientProfile clientprofile = clientProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        ServiceProviderProfile serviceProviderProfile = serviceProviderProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Booking booking = new Booking();
        booking.setClientProfile(clientprofile);
        booking.setProviderProfile(serviceProviderProfile);
        booking.setDescription(request.getDescription());
        booking.setTimeSlot(request.getTimeSlot());
        booking.setEstimatedDuration(request.getEstimatedDuration());
        booking.setStatus(BookingStatus.PENDING);

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsForProvider(Long providerId) {
        return bookingRepository.findByProviderProfile_Id(providerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientBookingViewResponse> getBookingsForClient(UserDetails userDetails) {
        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        return bookingRepository.findByClientProfile_Id(clientProfile.getId())
                .stream()
                .map(this::mapToClientViewResponse)
                .collect(Collectors.toList());
    }

    private ClientBookingViewResponse mapToClientViewResponse(Booking booking) {
        ClientBookingViewResponse response = new ClientBookingViewResponse();
        User providerUser = booking.getProviderProfile().getUser();

        response.setId(booking.getId());
        response.setProviderFullName(providerUser.getFirstName() + " " + providerUser.getLastName());
        response.setOccupation(booking.getProviderProfile().getOccupation());

        response.setCity(providerUser.getCity());
        response.setState(providerUser.getState());
        response.setCountry(providerUser.getCountry());
        response.setStatus(booking.getStatus().name());
        response.setTimeAgo(getTimeAgo(booking.getCreatedAt()));

        return response;
    }

    @Override
    public void updateStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatus.valueOf(status.toUpperCase()));
        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setClientFullName(booking.getClientProfile().getUser().getFirstName() + " " + booking.getClientProfile().getUser().getLastName());
        response.setClientCompany(booking.getClientProfile().getCompanyName());
        response.setClientPosition(booking.getClientProfile().getPosition());
        response.setCity(booking.getClientProfile().getUser().getCity());
        response.setState(booking.getClientProfile().getUser().getState());
        response.setCountry(booking.getClientProfile().getUser().getCountry());


        response.setDescription(booking.getDescription());
        response.setTimeSlot(booking.getTimeSlot());
        response.setEstimatedDuration(booking.getEstimatedDuration());
        response.setStatus(booking.getStatus().name());

        response.setTimeAgo(getTimeAgo(booking.getCreatedAt()));
        return response;
    }

    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return seconds + " seconds ago";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        long days = hours / 24;
        if (days < 30) return days + " days ago";
        long months = days / 30;
        return months + " months ago";
    }
}




