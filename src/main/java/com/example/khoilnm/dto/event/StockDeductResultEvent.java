package com.example.khoilnm.dto.event;

/** Payload of topic stock-deduct-result. */
public record StockDeductResultEvent(String orderId, Status status, String reason) {

    public enum Status {
        SUCCESS,
        FAILED
    }

    public static StockDeductResultEvent success(String orderId) {
        return new StockDeductResultEvent(orderId, Status.SUCCESS, null);
    }

    public static StockDeductResultEvent failed(String orderId, String reason) {
        return new StockDeductResultEvent(orderId, Status.FAILED, reason);
    }
}
