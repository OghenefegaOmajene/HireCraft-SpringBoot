//package HireCraft.com.SpringBoot.services.impl;
//
//import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
//import HireCraft.com.SpringBoot.dtos.requests.UpdateBookingStatusRequest;
//import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
//import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
//import HireCraft.com.SpringBoot.enums.BookingStatus;
//import HireCraft.com.SpringBoot.exceptions.InvalidBookingStatusTransitionException;
//import HireCraft.com.SpringBoot.exceptions.UnauthorizedBookingActionException;
//import HireCraft.com.SpringBoot.models.Booking;
//import HireCraft.com.SpringBoot.models.ClientProfile;
//import HireCraft.com.SpringBoot.models.ServiceProviderProfile;
//import HireCraft.com.SpringBoot.models.User;
//import HireCraft.com.SpringBoot.repository.BookingRepository;
//import HireCraft.com.SpringBoot.repository.ClientProfileRepository;
//import HireCraft.com.SpringBoot.repository.ServiceProviderProfileRepository;
//import HireCraft.com.SpringBoot.repository.UserRepository;
//import HireCraft.com.SpringBoot.services.BookingService;
//
//import HireCraft.com.SpringBoot.models.Message;
//import HireCraft.com.SpringBoot.repository.MessageRepository;
//import HireCraft.com.SpringBoot.utils.EncryptorUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.transaction.annotation.Transactional;
//
//
//@Service
//@RequiredArgsConstructor
//public class BookingServiceImpl implements BookingService {
//
//    private final UserRepository userRepository;
//    private final BookingRepository bookingRepository;
//    private final ClientProfileRepository clientProfileRepository;
//    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
//    private final MessageRepository messageRepository;
//    private final EncryptorUtil encryptorUtil;
//
//    @Override
//    public BookingResponse createBooking(BookingRequest request, UserDetails userDetails) {
//        ClientProfile clientprofile = clientProfileRepository.findByUserEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Client profile not found"));
//
//        ServiceProviderProfile serviceProviderProfile = serviceProviderProfileRepository.findById(request.getProviderId())
//                .orElseThrow(() -> new RuntimeException("Provider not found"));
//
//        Booking booking = new Booking();
//        booking.setClientProfile(clientprofile);
//        booking.setProviderProfile(serviceProviderProfile);
//        booking.setDescription(request.getDescription());
//        booking.setTimeSlot(request.getTimeSlot());
//        booking.setEstimatedDuration(request.getEstimatedDuration());
//        booking.setStatus(BookingStatus.PENDING);
//
//        bookingRepository.save(booking);
//
//        Message message = new Message();
//        message.setBooking(booking);
//        message.setClientProfile(clientprofile); // Sender is the client
//        message.setEncryptedContent(encryptorUtil.encrypt(request.getDescription()));
//        messageRepository.save(message);
//
//        return mapToBookingResponse(booking);
//    }
//
//    @Override
//    public List<BookingResponse> getBookingsForProvider(UserDetails userDetails) {
//        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Provider profile not found"));
//
//        return bookingRepository.findByProviderProfile_Id(providerProfile.getId())
//                .stream()
//                .map(this::mapToBookingResponse)
//                .collect(Collectors.toList());
//    }
//
//    private BookingResponse mapToBookingResponse(Booking booking) {
//        BookingResponse response = new BookingResponse();
//        User clientUser = booking.getClientProfile().getUser();
//
//        response.setId(booking.getId());
//        response.setClientFullName(clientUser.getFirstName() + " " + clientUser.getLastName());
//        response.setClientPosition(booking.getClientProfile().getPosition());
//        response.setClientCompany(clientUser.getClientProfile().getCompanyName());
//        response.setCity(clientUser.getCity());
//        response.setState(clientUser.getState());
//        response.setCountry(clientUser.getCountry());
//        response.setTimeSlot(booking.getTimeSlot());
//        response.setEstimatedDuration(booking.getEstimatedDuration());
//        response.setDescription(booking.getDescription());
//        response.setStatus(booking.getStatus().name());
//        response.setTimeAgo(getTimeAgo(booking.getUpdatedAt()));
//
//        return response;
//    }
//
//    @Override
//    public List<ClientBookingViewResponse> getBookingsForClient(UserDetails userDetails) {
//        ClientProfile clientProfile = clientProfileRepository.findByUserEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Client profile not found"));
//
//        return bookingRepository.findByClientProfile_Id(clientProfile.getId())
//                .stream()
//                .map(this::mapToClientViewResponse)
//                .collect(Collectors.toList());
//    }
//
//    private ClientBookingViewResponse mapToClientViewResponse(Booking booking) {
//        ClientBookingViewResponse response = new ClientBookingViewResponse();
//        User providerUser = booking.getProviderProfile().getUser();
//
//        response.setId(booking.getId());
//        response.setProviderFullName(providerUser.getFirstName() + " " + providerUser.getLastName());
//        response.setOccupation(booking.getProviderProfile().getOccupation());
//
//        response.setCity(providerUser.getCity());
//        response.setState(providerUser.getState());
//        response.setCountry(providerUser.getCountry());
//        response.setStatus(booking.getStatus().name());
//        response.setTimeAgo(getTimeAgo(booking.getUpdatedAt()));
//
//        return response;
//    }
//
//    @Override
//    @Transactional // Ensure the status update is atomic
//    public BookingResponse updateBookingStatus(Long bookingId, UpdateBookingStatusRequest request, UserDetails userDetails) {
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
//
//        // Get the authenticated user's actual User entity
//        User authenticatedUser = userRepository.findByEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//
//        // Determine if the authenticated user is the client of this booking
//        boolean isClientOfBooking = booking.getClientProfile().getUser().getId().equals(authenticatedUser.getId());
//        // Determine if the authenticated user is the provider of this booking
//        boolean isProviderOfBooking = booking.getProviderProfile().getUser().getId().equals(authenticatedUser.getId());
//
//        // Initial authorization check: User must be either the client or the provider of the booking
//        if (!isClientOfBooking && !isProviderOfBooking) {
//            throw new UnauthorizedBookingActionException("You are not authorized to update this booking.");
//        }
//
//        BookingStatus currentStatus = booking.getStatus();
//        BookingStatus newStatus = request.getNewStatus();
//
//        // --- Business Logic for Status Transitions based on Role ---
//
//        if (authenticatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"))) {
//            // Logic for Client
//            if (!isClientOfBooking) {
//                throw new UnauthorizedBookingActionException("As a client, you can only cancel your own bookings.");
//            }
//            if (newStatus == BookingStatus.CANCELLED) {
//                // Client can only cancel if PENDING or ACCEPTED
//                if (currentStatus == BookingStatus.PENDING || currentStatus == BookingStatus.ACCEPTED) {
//                    booking.setStatus(newStatus);
//                } else {
//                    throw new InvalidBookingStatusTransitionException(
//                            "Cannot cancel a booking that is " + currentStatus.name() + ". Only PENDING or ACCEPTED bookings can be cancelled."
//                    );
//                }
//            } else {
//                throw new InvalidBookingStatusTransitionException("Clients can only change booking status to CANCELLED.");
//            }
//        } else if (authenticatedUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_PROVIDER"))) {
//            // Logic for Provider
//            if (!isProviderOfBooking) {
//                throw new UnauthorizedBookingActionException("As a provider, you can only manage your own service bookings.");
//            }
//            switch (newStatus) {
//                case ACCEPTED:
//                    if (currentStatus == BookingStatus.PENDING) {
//                        booking.setStatus(newStatus);
//                    } else {
//                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be ACCEPTED.");
//                    }
//                    break;
//                case DECLINED:
//                    if (currentStatus == BookingStatus.PENDING) {
//                        booking.setStatus(newStatus);
//                    } else {
//                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be DECLINED.");
//                    }
//                    break;
//                case COMPLETED:
//                    if (currentStatus == BookingStatus.ACCEPTED) {
//                        booking.setStatus(newStatus);
//                    } else {
//                        throw new InvalidBookingStatusTransitionException("Booking must be ACCEPTED to be COMPLETED.");
//                    }
//                    break;
//                default:
//                    throw new InvalidBookingStatusTransitionException("Providers can only change booking status to ACCEPTED, DECLINED, or COMPLETED.");
//            }
//        } else {
//            // This case should ideally be caught by @PreAuthorize, but as a fallback.
//            throw new UnauthorizedBookingActionException("Your role does not permit updating booking statuses.");
//        }
//
//        // Save the updated booking
//        Booking updatedBooking = bookingRepository.save(booking);
//        return mapToBookingResponse(updatedBooking);
//    }
//
//    private BookingResponse mapToResponse(Booking booking) {
//        BookingResponse response = new BookingResponse();
//        response.setId(booking.getId());
//        response.setClientFullName(booking.getClientProfile().getUser().getFirstName() + " " + booking.getClientProfile().getUser().getLastName());
//        response.setClientCompany(booking.getClientProfile().getCompanyName());
//        response.setClientPosition(booking.getClientProfile().getPosition());
//        response.setCity(booking.getClientProfile().getUser().getCity());
//        response.setState(booking.getClientProfile().getUser().getState());
//        response.setCountry(booking.getClientProfile().getUser().getCountry());
//
//
//        response.setDescription(booking.getDescription());
//        response.setTimeSlot(booking.getTimeSlot());
//        response.setEstimatedDuration(booking.getEstimatedDuration());
//        response.setStatus(booking.getStatus().name());
//
//        response.setTimeAgo(getTimeAgo(booking.getCreatedAt()));
//        return response;
//    }
//
//    private String getTimeAgo(LocalDateTime timestamp){
//        Duration duration = Duration.between(timestamp, LocalDateTime.now());
//        long seconds = duration.getSeconds();
//
//        if (seconds < 60) return seconds + " seconds ago";
//        long minutes = seconds / 60;
//        if (minutes < 60) return minutes + " minutes ago";
//        long hours = minutes / 60;
//        if (hours < 24) return hours + " hours ago";
//        long days = hours / 24;
//        if (days < 30) return days + " days ago";
//        long months = days / 30;
//        return months + " months ago";
//    }
//}
//
//
//
//


package HireCraft.com.SpringBoot.services.impl;

import HireCraft.com.SpringBoot.dtos.requests.BookingRequest;
import HireCraft.com.SpringBoot.dtos.requests.NotificationRequest;
import HireCraft.com.SpringBoot.dtos.requests.UpdateBookingStatusRequest;
import HireCraft.com.SpringBoot.dtos.response.BookingChartResponse;
import HireCraft.com.SpringBoot.dtos.response.BookingResponse;
import HireCraft.com.SpringBoot.dtos.response.ClientBookingViewResponse;
import HireCraft.com.SpringBoot.dtos.response.ProviderDashboardMetricsResponse;
import HireCraft.com.SpringBoot.enums.BookingStatus;
import HireCraft.com.SpringBoot.enums.NotificationType;
import HireCraft.com.SpringBoot.enums.ReferenceType;
import HireCraft.com.SpringBoot.exceptions.InvalidBookingStatusTransitionException;
import HireCraft.com.SpringBoot.exceptions.UnauthorizedBookingActionException;
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
import HireCraft.com.SpringBoot.services.NotificationService;
import HireCraft.com.SpringBoot.services.ReviewService;
import HireCraft.com.SpringBoot.utils.EncryptorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;


@Service
//@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final ServiceProviderProfileRepository serviceProviderProfileRepository;
    private final MessageRepository messageRepository;
    private final EncryptorUtil encryptorUtil;
    private final ReviewService reviewService;
    private final NotificationService notificationService;

    public BookingServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, ClientProfileRepository clientProfileRepository, ServiceProviderProfileRepository serviceProviderProfileRepository, MessageRepository messageRepository, EncryptorUtil encryptorUtil, ReviewService reviewService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.serviceProviderProfileRepository = serviceProviderProfileRepository;
        this.messageRepository = messageRepository;
        this.encryptorUtil = encryptorUtil;
        this.reviewService = reviewService;
        this.notificationService = notificationService;
    }

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

        Booking savedBooking = bookingRepository.save(booking);

        Message message = new Message();
        message.setBooking(booking);
        message.setClientProfile(clientprofile); // Sender is the client
        message.setEncryptedContent(encryptorUtil.encrypt(request.getDescription()));
        messageRepository.save(message);

        Long providerUserId = serviceProviderProfile.getUser().getId();
        String clientFullName = clientprofile.getUser().getFirstName() + " " + clientprofile.getUser().getLastName();

        // Create a notification request for the provider
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .message(String.format("New booking request from %s", clientFullName))
//                .message(String.format("New booking request from %s for %s.", clientFullName, savedBooking.getTimeSlot().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))))
                .type(NotificationType.NEW_BOOKING_REQUEST) // Assuming this enum value exists
                .userId(providerUserId)
                .referenceId(savedBooking.getId())
                .referenceType(ReferenceType.BOOKING)
                .build();
        notificationService.createNotification(notificationRequest);

        return mapToBookingResponse(savedBooking);
    }

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
        // Added null check for getClientProfile().getCompanyName()
        response.setClientCompany(clientUser.getClientProfile() != null ? clientUser.getClientProfile().getCompanyName() : null);
        response.setCity(clientUser.getCity());
        response.setState(clientUser.getState());
        response.setCountry(clientUser.getCountry());
        response.setTimeSlot(booking.getTimeSlot());
        response.setEstimatedDuration(booking.getEstimatedDuration());
        response.setDescription(booking.getDescription());
        response.setStatus(booking.getStatus().name());
        // *** CHANGED THIS LINE ***
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
        // Added null check for getProviderProfile().getOccupation()
        response.setOccupation(booking.getProviderProfile() != null ? booking.getProviderProfile().getOccupation() : null);


        response.setCity(providerUser.getCity());
        response.setState(providerUser.getState());
        response.setCountry(providerUser.getCountry());
        response.setStatus(booking.getStatus().name());
        // *** CHANGED THIS LINE ***
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
                    } else {
                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be ACCEPTED.");
                    }
                    break;
                case DECLINED:
                    if (currentStatus == BookingStatus.PENDING) {
                        booking.setStatus(newStatus);
                    } else {
                        throw new InvalidBookingStatusTransitionException("Booking must be PENDING to be DECLINED.");
                    }
                    break;
                case COMPLETED:
                    if (currentStatus == BookingStatus.ACCEPTED) {
                        booking.setStatus(newStatus);
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
        return mapToBookingResponse(updatedBooking);
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

    private String getTimeAgo(LocalDateTime timestamp) {
        // Add a null check here to prevent NullPointerException
        if (timestamp == null) {
            return "N/A"; // Or any other suitable placeholder
        }

        Duration duration = Duration.between(timestamp, LocalDateTime.now());
        long seconds = duration.getSeconds();

        // Handle future dates (in case of clock skew)
        if (seconds < 0) {
            return "just now";
        }

        // Less than 1 minute
        if (seconds < 60) {
            if (seconds == 0) return "just now";
            return seconds == 1 ? "1 second ago" : seconds + " seconds ago";
        }

        // Less than 1 hour
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        }

        // Less than 1 day
        long hours = minutes / 60;
        if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }

        // Less than 30 days
        long days = hours / 24;
        if (days < 30) {
            return days == 1 ? "1 day ago" : days + " days ago";
        }

        // Less than 12 months
        long months = days / 30; // Rough approximation
        if (months < 12) {
            return months == 1 ? "1 month ago" : months + " months ago";
        }

        // Years
        long years = months / 12;
        return years == 1 ? "1 year ago" : years + " years ago";
    }

    // New methods implementation
    @Override
    public long getNewBookingRequestsCountToday(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        // Get start and end of today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        return bookingRepository.countNewBookingsForProviderByDate(providerProfile.getId(), startOfDay, endOfDay);
    }

    @Override
    public long getCompletedJobsCountForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return bookingRepository.countCompletedJobsForProvider(providerProfile.getId());
    }

    @Override
    public long countCompletedJobsForProvider(Long providerId) {
        return bookingRepository.countCompletedJobsForProvider(providerId);
    }

    // --- NEW METHOD IMPLEMENTATION FOR COMPLETION RATE ---
    @Override
    public long getAcceptedJobsCountForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return bookingRepository.countAcceptedJobsForProvider(providerProfile.getId());
    }

    @Override
    public ProviderDashboardMetricsResponse getProviderDashboardMetrics(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        // Fetch individual counts
        long newBookingRequestsToday = getNewBookingRequestsCountToday(userDetails); // Re-use existing method
        long completedJobs = getCompletedJobsCountForProvider(userDetails); // Re-use existing method
        long acceptedJobs = getAcceptedJobsCountForProvider(userDetails); // Re-use existing method
        long rejectedJobs = getRejectedJobsCountForProvider(userDetails);
        long totalReviews = reviewService.getReviewCountForProvider(userDetails); // Use ReviewService for reviews

        // Get average rating directly from ServiceProviderProfile
        double averageRating = providerProfile.getAverageRating(); // Assuming this is updated by your review service

        // TODO: Implement actual logic for daily earnings and unread messages
        // For now, they will be 0 or mock data
        double dailyEarnings = 0.0;
        long unreadMessages = 0; // You'd need a MessageRepository method to count unread messages for this provider

        return ProviderDashboardMetricsResponse.builder()
                .newBookingRequestsToday(newBookingRequestsToday)
                .completedJobs(completedJobs)
                .acceptedJobs(acceptedJobs)
                .rejectedJobs(rejectedJobs)
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .dailyEarnings(dailyEarnings)
                .unreadMessages(unreadMessages)
                .build();
    }


    @Override
    public List<BookingChartResponse> getMonthlyBookingChart(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        // Get the last 6 months
        List<BookingChartResponse> chartData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = today.minusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = monthStart.plusMonths(1).withDayOfMonth(1);

            LocalDateTime startOfMonth = monthStart.atStartOfDay();
            LocalDateTime endOfMonth = monthEnd.atStartOfDay();

            // Get counts for each status for this month
            List<BookingStatus> acceptedStatuses = Arrays.asList(
                    BookingStatus.ACCEPTED,
                    BookingStatus.COMPLETED,
                    BookingStatus.CANCELLED
            );
            long acceptedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
                    providerProfile.getId(), acceptedStatuses, startOfMonth, endOfMonth
            );

            long completedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
                    providerProfile.getId(), Collections.singletonList(BookingStatus.COMPLETED), startOfMonth, endOfMonth);

            long rejectedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
                    providerProfile.getId(), Collections.singletonList(BookingStatus.DECLINED), startOfMonth, endOfMonth);

            // Format the month for display
            String monthName = monthStart.format(DateTimeFormatter.ofPattern("MMM")); // Jan, Feb, etc.
            String fullDate = monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy")); // Jan 2024

            BookingChartResponse chartResponse = BookingChartResponse.builder()
                    .month(monthName)
                    .fullDate(fullDate)
                    .acceptedBookings(acceptedCount)
                    .completedBookings(completedCount)
                    .rejectedBookings(rejectedCount)
                    .build();

            chartData.add(chartResponse);
        }

        return chartData;
    }
//    @Override
//    public List<BookingChartResponse> getWeeklyBookingChart(UserDetails userDetails) {
//        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
//                .orElseThrow(() -> new RuntimeException("Provider profile not found"));
//
//        // Get the last 7 days
//        List<BookingChartResponse> chartData = new ArrayList<>();
//        LocalDate today = LocalDate.now();
//
//        for (int i = 6; i >= 0; i--) {
//            LocalDate date = today.minusDays(i);
//            LocalDateTime startOfDay = date.atStartOfDay();
//            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
//
//            // Get counts for each status on this date
//            long acceptedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
//                    providerProfile.getId(), BookingStatus.ACCEPTED, startOfDay, endOfDay);
//
//            long completedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
//                    providerProfile.getId(), BookingStatus.COMPLETED, startOfDay, endOfDay);
//
//            long rejectedCount = bookingRepository.countBookingsByProviderAndStatusAndDateRange(
//                    providerProfile.getId(), BookingStatus.DECLINED, startOfDay, endOfDay);
//
//            // Format the date for display
//            String dayName = date.format(DateTimeFormatter.ofPattern("EEE")); // Mon, Tue, etc.
//            String fullDate = date.format(DateTimeFormatter.ofPattern("MMM d")); // Jan 15
//
//            BookingChartResponse chartResponse = BookingChartResponse.builder()
//                    .date(dayName)
//                    .fullDate(fullDate)
//                    .acceptedBookings(acceptedCount)
//                    .completedBookings(completedCount)
//                    .rejectedBookings(rejectedCount)
//                    .build();
//
//            chartData.add(chartResponse);
//        }
//
//        return chartData;
//    }

    // Add this method to get rejected jobs count for dashboard metrics
    @Override
    public long getRejectedJobsCountForProvider(UserDetails userDetails) {
        ServiceProviderProfile providerProfile = serviceProviderProfileRepository.findByUserEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));

        return bookingRepository.countRejectedJobsForProvider(providerProfile.getId());
    }
}