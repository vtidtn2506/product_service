package com.example.khoilnm.constant;

public final class KafkaTopic {
    /** Produced by order_service after an order is created. Key = orderId. Consumed here to deduct stock. */
    public static final String ORDER_CREATE = "order-create";

    /** Result of the stock deduction of an order, consumed by order_service. Key = orderId. */
    public static final String STOCK_DEDUCT_RESULT = "stock-deduct-result";

    private KafkaTopic() {
    }
}
