package com.example.khoilnm.dto.event;

import java.time.Instant;
import java.util.List;

/** Payload of topic order-create (same JSON shape as order_service's OrderCreatedEvent). */
public record OrderCreatedEvent(
        String orderId,
        String customerId,
        String status,
        int amount,
        List<Item> items,
        Instant createdDate,
        String createdBy
) {
    public record Item(String orderItemId, String productId, int price, int quantity) {
    }
}
