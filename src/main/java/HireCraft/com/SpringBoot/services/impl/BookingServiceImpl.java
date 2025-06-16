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

import HireCraft.com.SpringBoot.models.Message;
import HireCraft.com.SpringBoot.repository.MessageRepository;
import HireCraft.com.SpringBoot.utils.EncryptorUtil;
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
    private final MessageRepository messageRepository;
    private final EncryptorUtil encryptorUtil;

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

        Message message = new Message();
        message.setBooking(booking);
        message.setClientProfile(clientprofile); // Sender is the client
        message.setEncryptedContent(encryptorUtil.encrypt(request.getDescription()));
        messageRepository.save(message);

        return mapToResponse(booking);
    }

//    @Override
//    public List<BookingResponse> getBookingsForProvider(Long providerId) {
//        return bookingRepository.findByProviderProfile_Id(providerId)
//                .stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }

    @Override
    public List<BookingResponse> getBookingsForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return bookingRepository.findByProviderProfile_Id(providerProfile.getId())
                .stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        User clientUser = booking.getClientProfile().getUser();

        response.setId(booking.getId());
        response.setClientFullName(clientUser.getFirstName() + " " + clientUser.getLastName());
        response.setClientPosition(booking.getClientProfile().getPosition());
        response.setClientCompany(clientUser.getClientProfile().getCompanyName());
        response.setCity(clientUser.getCity());
        response.setState(clientUser.getState());
        response.setCountry(clientUser.getCountry());
        response.setTimeSlot(booking.getTimeSlot());
        response.setEstimatedDuration(booking.getEstimatedDuration());
        response.setDescription(booking.getDescription());
        response.setStatus(booking.getStatus().name());
        response.setTimeAgo(getTimeAgo(booking.getCreatedAt()));

        return response;
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
    @Transactional // Ensure the status update is atomic
    public BookingResponse updateBookingStatus(Long bookingId, UpdateBookingStatusRequest request, UserDetails userDetails) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

        // Get the authenticated user's actual User entity
        User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Determine if the authenticated user is the client of this booking
        boolean isClientOfBooking = booking.getClientProfile().getUser().getId().equals(authenticatedUser.getId());
        // Determine if the authenticated user is the provider of this booking
        boolean isProviderOfBooking = booking.getProviderProfile().getUser().getId().equals(authenticatedUser.getId());

        // Initial authorization check: User must be either the client or the provider of the booking
        if (!isClientOfBooking && !isProviderOfBooking) {
            throw new UnauthorizedBookingActionException("You are not authorized to update this booking.");
        }

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus newStatus = request.getNewStatus();

        // --- Business Logic for Status Transitions based on Role ---

        if (authenticatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"))) {
            // Logic for Client
            if (!isClientOfBooking) {
                throw new UnauthorizedBookingActionException("As a client, you can only cancel your own bookings.");
            }
            if (newStatus == BookingStatus.CANCELLED) {
                // Client can only cancel if PENDING or ACCEPTED
                if (currentStatus == BookingStatus.PENDING || currentStatus == BookingStatus.ACCEPTED) {
                    booking.setStatus(newStatus);
                    booking.setUpdatedAt(LocalDateTime.now());
                } else {
                    throw new InvalidBookingStatusTransitionException(
                            "Cannot cancel a booking that is " + currentStatus.name() + ". Only PENDING or ACCEPTED bookings can be cancelled."
                    );
                }
            } else {
                throw new InvalidBookingStatusTransitionException("Clients can only change booking status to CANCELLED.");
            }
        } else if (authenticatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_PROVIDER"))) {
            // Logic for Provider
            if (!isProviderOfBooking) {
                throw new UnauthorizedBookingActionException("As a provider, you can only manage your own service bookings.");
            }
            switch (newStatus) {
                case ACCEPTED:
                    if (currentStatus == BookingStatus.PENDING) {
                        booking.setStatus(newStatus);
                        booking.setUpdatedAt(LocalDateTime.now());
                    } else {
                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be ACCEPTED.");
                    }
                    break;
                case DECLINED:
                    if (currentStatus == BookingStatus.PENDING) {
                        booking.setStatus(newStatus);
                        booking.setUpdatedAt(LocalDateTime.now());
                    } else {
                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be DECLINED.");
                    }
                    break;
                case COMPLETED:
                    if (currentStatus == BookingStatus.ACCEPTED) {
                        booking.setStatus(newStatus);
                        booking.setUpdatedAt(LocalDateTime.now());
                    } else {
                        throw new InvalidBookingStatusTransitionException("Booking must be ACCEPTED to be COMPLETED.");
                    }
                    break;
                default:
                    throw new InvalidBookingStatusTransitionException("Providers can only change booking status to ACCEPTED, DECLINED, or COMPLETED.");
            }
        } else {
            // This case should ideally be caught by @PreAuthorize, but as a fallback.
            throw new UnauthorizedBookingActionException("Your role does not permit updating booking statuses.");
        }

        // Save the updated booking
        Booking updatedBooking = bookingRepository.save(booking);

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




