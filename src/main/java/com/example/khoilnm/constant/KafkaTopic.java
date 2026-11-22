package com.example.khoilnm.constant;

public final class KafkaTopic {
    /** Produced by order_service after an order is created. Key = orderId. Consumed here to deduct stock. */
    public static final String ORDER_CREATE = "order-create";

    private KafkaTopic() {
    }
}
