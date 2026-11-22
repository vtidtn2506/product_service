package com.example.khoilnm.entity;

import com.khoilnm.commoncore.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Marks an order whose stock has already been deducted (idempotency for the Kafka consumer). */
@Entity
@Table(name = "stock_deduction", schema = "product")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockDeduction extends BaseEntity {

    @Column(name = "order_id")
    private String orderId;
}
