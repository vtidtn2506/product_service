package com.example.khoilnm.consumer;

import com.example.khoilnm.constant.KafkaTopic;
import com.example.khoilnm.dto.event.OrderCreatedEvent;
import com.example.khoilnm.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;


/**
 * Deducts the stock of every created order.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {
    private final ProductService productService;

    @KafkaListener(topics = KafkaTopic.ORDER_CREATE)
    public void onOrderCreated(ConsumerRecord<String, OrderCreatedEvent> record) {
        OrderCreatedEvent event = record.value();
        log.info("Received {} (partition={}, offset={}): orderId={}, customerId={}, amount={}, items={}",
                record.topic(), record.partition(), record.offset(),
                event.orderId(), event.customerId(), event.amount(), event.items().size());
        productService.deductStockForOrder(event);
    }

    @Bean
    public static DefaultErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(
                (record, ex) -> log.error("Giving up on {} (partition={}, offset={}, key={}): {}",
                        record.topic(), record.partition(), record.offset(), record.key(),
                        NestedExceptionUtils.getMostSpecificCause(ex).getMessage()),
                new FixedBackOff(1000L, 3));
    }
}
