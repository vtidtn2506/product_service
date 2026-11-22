package com.example.khoilnm.service.impl;

import com.example.khoilnm.dto.event.OrderCreatedEvent;
import com.example.khoilnm.dto.response.ProductResponse;
import com.example.khoilnm.entity.Product;
import com.example.khoilnm.entity.StockDeduction;
import com.example.khoilnm.mapper.ProductMapper;
import com.example.khoilnm.repository.ProductRepository;
import com.example.khoilnm.repository.StockDeductionRepository;
import com.example.khoilnm.service.ProductService;
import com.khoilnm.commoncore.exception.BusinessException;
import com.khoilnm.commoncore.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.khoilnm.constant.ProductResponseCode.INSUFFICIENT_STOCK;
import static com.example.khoilnm.constant.ProductResponseCode.PRODUCT_NOT_FOUND;
import static com.example.khoilnm.constant.ProductResponseCode.STOCK_LOCK_TIMEOUT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final int NOT_DELETED = 0;

    private final ProductRepository productRepository;
    private final StockDeductionRepository stockDeductionRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProducts() {
        return productMapper.toResponses(productRepository.findAllByIsDelete(NOT_DELETED));
    }

    @Override
    public List<ProductResponse> getProductsByIds(List<String> ids) {
        return productMapper.toResponses(productRepository.findAllByIdInAndIsDelete(ids, NOT_DELETED));
    }

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findByIdAndIsDelete(id, NOT_DELETED)
                .orElseThrow(() -> businessException(PRODUCT_NOT_FOUND, "Product not found: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deductStockForOrder(OrderCreatedEvent event) {
        if (stockDeductionRepository.existsByOrderId(event.orderId())) {
            log.info("Stock of order {} already deducted, ignoring duplicate event", event.orderId());
            return;
        }

        // Sum quantities per product (the same product may appear in several items);
        // TreeMap = rows are always locked in the same order → no deadlock between orders
        deduct(event.items().stream()
                .collect(Collectors.toMap(OrderCreatedEvent.Item::productId,
                        OrderCreatedEvent.Item::quantity, Math::addExact, TreeMap::new)));

        // Same transaction as the deduction: both commit or neither. If two copies of the event are
        // processed at the same time, UNIQUE(order_id) makes the second one fail and roll back.
        stockDeductionRepository.saveAndFlush(StockDeduction.builder().orderId(event.orderId()).build());
        log.info("Deducted stock for order {}", event.orderId());
    }

    private void deduct(Map<String, Integer> quantities) {
        Map<String, Product> products = lockProducts(quantities);

        List<String> missing = quantities.keySet().stream().filter(id -> !products.containsKey(id)).toList();
        if (!missing.isEmpty()) {
            throw businessException(PRODUCT_NOT_FOUND, "Product not found: " + String.join(", ", missing));
        }

        // Rows are locked: the stock read here can't change until this transaction ends → no oversell
        List<String> insufficient = quantities.entrySet().stream()
                .filter(entry -> entry.getValue() > products.get(entry.getKey()).getStock())
                .map(entry -> {
                    Product product = products.get(entry.getKey());
                    return "%s (id=%s, requested=%d, stock=%d)".formatted(
                            product.getTitle(), product.getId(), entry.getValue(), product.getStock());
                })
                .toList();
        if (!insufficient.isEmpty()) {
            throw businessException(INSUFFICIENT_STOCK, "Insufficient stock: " + String.join("; ", insufficient));
        }

        quantities.forEach((id, quantity) -> {
            Product product = products.get(id);
            product.setStock(product.getStock() - quantity);
        });
        List<Product> toSave = (List<Product>) products.values();
        productRepository.saveAll(toSave);
    }

    private Map<String, Product> lockProducts(Map<String, Integer> quantities) {
        try {
            return productRepository.findAllByIdInForUpdate(quantities.keySet()).stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));
        } catch (PessimisticLockingFailureException e) {
            log.warn("Timed out waiting for product lock {}: {}", quantities.keySet(), e.getMessage());
            throw businessException(STOCK_LOCK_TIMEOUT, "Product is busy, please retry");
        }
    }

    private static BusinessException businessException(ResponseCode code, String message) {
        return new BusinessException(code, message, code.getDefaultHttpStatus());
    }
}
