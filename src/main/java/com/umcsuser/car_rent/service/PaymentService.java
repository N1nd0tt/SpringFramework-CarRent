package com.umcsuser.car_rent.service;

public interface PaymentService {
    String createCheckoutSession(String rentalId);
    void handleWebhook(String payload, String signature);
}
