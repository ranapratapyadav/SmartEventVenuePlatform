package com.smartevent.booking.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartevent.booking.client.CustomerClient;
import com.smartevent.booking.client.EventClient;
import com.smartevent.booking.client.NotificationClient;

import com.smartevent.booking.dto.CustomerDto;
import com.smartevent.booking.dto.EventDto;
import com.smartevent.booking.dto.NotificationDto;
import com.smartevent.booking.dto.PaymentDto;
import com.smartevent.booking.dto.PaymentResponseDto;

import com.smartevent.booking.entity.Booking;
import com.smartevent.booking.entity.BookingStatus;
import com.smartevent.booking.entity.PaymentMode;

import com.smartevent.booking.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final CustomerClient customerClient;

    private final EventClient eventClient;

    private final NotificationClient notificationClient;

    private final PaymentResilienceService paymentResilienceService;

    public BookingService(
            BookingRepository bookingRepository,
            CustomerClient customerClient,
            EventClient eventClient,
            NotificationClient notificationClient,
            PaymentResilienceService paymentResilienceService) {

        this.bookingRepository = bookingRepository;
        this.customerClient = customerClient;
        this.eventClient = eventClient;
        this.notificationClient = notificationClient;
        this.paymentResilienceService = paymentResilienceService;
    }

    // =========================================================
    // CREATE BOOKING
    // =========================================================

    @CircuitBreaker(
            name = "paymentService",
            fallbackMethod = "paymentFallback"
    )

    // TEMPORARILY DISABLED FOR TIMEOUT TEST
     @Retry(name = "paymentService")

    public Booking createBooking(Booking booking) {

        // Check customer

        CustomerDto customer =
                customerClient.getCustomerById(
                        booking.getCustomerId()
                );

        if (customer == null) {
            throw new RuntimeException(
                    "Customer not found"
            );
        }

        // Check event

        EventDto event =
                eventClient.getEventById(
                        booking.getEventId()
                );

        if (event == null) {
            throw new RuntimeException(
                    "Event not found"
            );
        }

        // Validate number of seats

        if (booking.getNumberOfSeats() == null ||
                booking.getNumberOfSeats() <= 0) {

            throw new RuntimeException(
                    "Number of seats must be greater than 0"
            );
        }

        // Check available seats

        if (booking.getNumberOfSeats() >
                event.getAvailableSeats()) {

            throw new RuntimeException(
                    "Not enough seats available. Available seats: "
                            + event.getAvailableSeats()
            );
        }

        // Reserve seats

        eventClient.reserveSeats(
                booking.getEventId(),
                booking.getNumberOfSeats()
        );

        // Calculate total amount

        double totalAmount =
                event.getTicketPrice()
                        * booking.getNumberOfSeats();

        booking.setTotalAmount(totalAmount);

        // Set booking date

        booking.setBookingDate(
                LocalDateTime.now()
        );

        // Set initial booking status

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        // Save booking

        Booking savedBooking =
                bookingRepository.save(booking);

        // Create payment request

        PaymentDto paymentDto =
                new PaymentDto();

        paymentDto.setBookingId(
                savedBooking.getBookingId()
        );

        paymentDto.setAmount(
                savedBooking.getTotalAmount()
        );

        paymentDto.setPaymentMode(
                PaymentMode.UPI
        );

        // =====================================================
        // CALL PAYMENT SERVICE THROUGH RESILIENCE4J
        // =====================================================

        PaymentResponseDto paymentResponse =
                paymentResilienceService
                        .createPayment(paymentDto)
                        .join();

        // Return booking

        return savedBooking;
    }

    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }

    // =========================================================
    // GET BOOKING BY ID
    // =========================================================

    public Booking getBookingById(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found with ID: "
                                        + bookingId
                        )
                );
    }

    // =========================================================
    // UPDATE BOOKING STATUS
    // =========================================================

    public Booking updateBookingStatus(
            Long bookingId,
            BookingStatus status) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with ID: "
                                                + bookingId
                                )
                        );

        booking.setBookingStatus(status);

        return bookingRepository.save(booking);
    }

    // =========================================================
    // CANCEL BOOKING
    // =========================================================

    public Booking cancelBooking(Long bookingId) {

        Booking booking =
                bookingRepository.findById(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with ID: "
                                                + bookingId
                                )
                        );

        // Check if already cancelled

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking is already cancelled"
            );
        }

        // Only confirmed bookings can be cancelled

        if (booking.getBookingStatus()
                != BookingStatus.CONFIRMED) {

            throw new RuntimeException(
                    "Only confirmed bookings can be cancelled"
            );
        }

        // Release reserved seats

        eventClient.releaseSeats(
                booking.getEventId(),
                booking.getNumberOfSeats()
        );

        // Update booking status

        booking.setBookingStatus(
                BookingStatus.CANCELLED
        );

        Booking cancelledBooking =
                bookingRepository.save(booking);

        // Create cancellation notification

        NotificationDto notification =
                new NotificationDto();

        notification.setCustomerId(
                cancelledBooking.getCustomerId()
        );

        notification.setBookingId(
                cancelledBooking.getBookingId()
        );

        notification.setMessage(
                "Your booking has been cancelled successfully."
        );

        notification.setNotificationType(
                "BOOKING_CANCELLED"
        );

        // Send notification

        notificationClient.createNotification(
                notification
        );

        return cancelledBooking;
    }

    // =========================================================
    // PAYMENT FALLBACK
    // =========================================================

    public Booking paymentFallback(
            Booking booking,
            Throwable ex) {

        System.out.println(
                "Payment Service failed: "
                        + ex.getMessage()
        );

        // Release reserved seats

        eventClient.releaseSeats(
                booking.getEventId(),
                booking.getNumberOfSeats()
        );

        // Mark booking as failed

        booking.setBookingStatus(
                BookingStatus.FAILED
        );

        return bookingRepository.save(booking);
    }
}