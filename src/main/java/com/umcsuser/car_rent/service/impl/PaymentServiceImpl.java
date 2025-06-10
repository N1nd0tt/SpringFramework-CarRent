package com.umcsuser.car_rent.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.umcsuser.car_rent.models.Payment;
import com.umcsuser.car_rent.models.PaymentStatus;
import com.umcsuser.car_rent.models.Rental;
import com.umcsuser.car_rent.repository.PaymentRepository;
import com.umcsuser.car_rent.repository.RentalRepository;
import com.umcsuser.car_rent.service.PaymentService;
import com.umcsuser.car_rent.service.RentalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;

    @Value("${STRIPE_API_KEY}")
    private String apiKey;
    @Value("${WEBHOOK_SECRET}")
    private String webhookSecret;

    @Override
    @Transactional
    public String createCheckoutSession(String rentalId){
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("No rental with id: " + rentalId));
        Stripe.apiKey = apiKey;
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Rental " + rentalId)
                        .build();
        //metoda obliczania kwoty do zapłaty
        var rentDate = rental.getRentDate();
        var returnDate = rental.getReturnDate();

        if (rentDate == null || returnDate == null) {
            throw new IllegalStateException("Rent date or return date is missing for rental ID: " + rentalId);
        }

        var rentDateTime = LocalDate.parse(rentDate).atStartOfDay();
        var returnDateTime = LocalDate.parse(returnDate).atStartOfDay();
        var rentalDurationDays = Duration.between(rentDateTime, returnDateTime).toDays();

        if (rentalDurationDays < 0) {
            throw new IllegalStateException("Invalid rental duration for rental ID: " + rentalId);
        }

        var vehiclePrice = rental.getVehicle().getPrice();
        if (vehiclePrice < 0) {
            throw new IllegalStateException("Invalid vehicle price for rental ID: " + rentalId);
        }

        var amount = rentalDurationDays <= 1 ? vehiclePrice : vehiclePrice * rentalDurationDays;
        amount *= 100;
        //koniec metody

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("pln")
                        .setUnitAmount((long) (amount))
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(lineItem)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .putMetadata("rentalId", rentalId)
                .setSuccessUrl("http://localhost:8080/api/payments/success")
                .setCancelUrl("http://localhost:8080/api/payments/cancel")
                .build();

        try{
            Session session = Session.create(params);
            Payment payment = Payment.builder()
                    .id(UUID.randomUUID().toString())
                    .amount(amount)
                    .createdAt(LocalDateTime.now())
                    .rental(rental)
                    .stripeSessionId(session.getId())
                    .status(PaymentStatus.PENDING)
                    .build();
            paymentRepository.save(payment);
            return session.getUrl();
        } catch(Exception e) {
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature){
        Stripe.apiKey = apiKey;
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid signature", e);
        }
        if("checkout.session.completed".equals(event.getType())){
            StripeObject stripeObject =
                    event.getDataObjectDeserializer().getObject().orElseThrow();
            String sessionId = ((Session)stripeObject).getId();
            if(sessionId != null){
                paymentRepository.findByStripeSessionId(sessionId).ifPresent(payment -> {
                   payment.setStatus(PaymentStatus.PAID);
                   payment.setPaidAt(LocalDateTime.now());
                   paymentRepository.save(payment);
                   Rental rental = payment.getRental();
                   rentalService.returnRental(rental.getVehicle().getId(), rental.getUser().getId());
                });
            }
        }
    }
}
