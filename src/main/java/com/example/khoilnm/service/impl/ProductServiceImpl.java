package com.example.khoilnm.service.impl;

import com.example.khoilnm.constant.KafkaTopic;
import com.example.khoilnm.dto.event.OrderCreatedEvent;
import com.example.khoilnm.dto.event.StockDeductResultEvent;
import com.example.khoilnm.dto.response.ProductResponse;
import com.example.khoilnm.entity.Product;
import com.example.khoilnm.mapper.ProductMapper;
import com.example.khoilnm.repository.ProductRepository;
import com.example.khoilnm.service.ProductService;
import com.khoilnm.commoncore.exception.BusinessException;
import com.khoilnm.commoncore.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.khoilnm.constant.ProductResponseCode.INSUFFICIENT_STOCK;
import static com.example.khoilnm.constant.ProductResponseCode.PRODUCT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final int NOT_DELETED = 0;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedissonClient redissonClient;

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
        List<String> sortedIds = event.items().stream().map(OrderCreatedEvent.Item::productId).sorted().toList();
        String lockKey = "lock:products:" + String.join(",", sortedIds);
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("Khong lay duoc lock");
            }
            deduct(event.items().stream()
                    .collect(Collectors.toMap(OrderCreatedEvent.Item::productId,
                            OrderCreatedEvent.Item::quantity, Math::addExact, TreeMap::new)));
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        StockDeductResultEvent result = new StockDeductResultEvent(event.orderId(), StockDeductResultEvent.Status.SUCCESS, "");
        publish(KafkaTopic.STOCK_DEDUCT_RESULT, event.orderId(), result);
    }

    private void publish(String topic, String orderId, Object payload) {
        kafkaTemplate.send(topic, orderId, payload).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish {} for order {}", topic, orderId, ex);
            } else {
                log.info("Published {} for order {} (partition={}, offset={})", topic, orderId,
                        result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }

    private void deduct(Map<String, Integer> quantities) {
        Map<String, Product> products = productRepository.findAllByIdInAndIsDelete(quantities.keySet(), NOT_DELETED)
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<String> missing = quantities.keySet().stream().filter(id -> !products.containsKey(id)).toList();
        if (!missing.isEmpty()) {
            throw businessException(PRODUCT_NOT_FOUND, "Product not found: " + String.join(", ", missing));
        }

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
        productRepository.saveAll(products.values());
    }

//    private Map<String, Product> lockProducts(Map<String, Integer> quantities) {
//        try {
//            return productRepository.findAllByIdInForUpdate(quantities.keySet()).stream()
//                    .collect(Collectors.toMap(Product::getId, Function.identity()));
//        } catch (PessimisticLockingFailureException e) {
//            log.warn("Timed out waiting for product lock {}: {}", quantities.keySet(), e.getMessage());
//            throw businessException(STOCK_LOCK_TIMEOUT, "Product is busy, please retry");
//        }
//    }

    private static BusinessException businessException(ResponseCode code, String message) {
        return new BusinessException(code, message, code.getDefaultHttpStatus());
    }
}
