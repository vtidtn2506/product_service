package com.example.khoilnm.repository;

import com.example.khoilnm.entity.StockDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDeductionRepository extends JpaRepository<StockDeduction, String> {
    boolean existsByOrderId(String orderId);
}
