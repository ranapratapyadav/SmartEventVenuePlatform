package com.smartevent.booking.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.smartevent.booking.client.PaymentClient;
import com.smartevent.booking.dto.PaymentDto;
import com.smartevent.booking.dto.PaymentResponseDto;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@Service
public class PaymentResilienceService {

    private final PaymentClient paymentClient;

    public PaymentResilienceService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @TimeLimiter(
        name = "paymentService",
        fallbackMethod = "paymentTimeoutFallback"
    )
    public CompletableFuture<PaymentResponseDto> createPayment(
            PaymentDto payment) {

        return CompletableFuture.supplyAsync(
            () -> paymentClient.createPayment(payment)
        );
    }

    public CompletableFuture<PaymentResponseDto> paymentTimeoutFallback(
            PaymentDto payment,
            Throwable ex) {

        System.out.println(
            "Payment Service timeout/unavailable: " + ex.getMessage()
        );

        CompletableFuture<PaymentResponseDto> failed =
                new CompletableFuture<>();

        failed.completeExceptionally(ex);

        return failed;
    }
}