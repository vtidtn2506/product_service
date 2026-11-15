package com.example.khoilnm.consumer;

import com.example.khoilnm.constant.KafkaTopic;
import com.example.khoilnm.dto.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreatedConsumer {

    @KafkaListener(topics = KafkaTopic.ORDER_CREATE)
    public void onOrderCreated(ConsumerRecord<String, OrderCreatedEvent> record) {
        OrderCreatedEvent event = record.value();
        log.info("Received {} (partition={}, offset={}): orderId={}, customerId={}, amount={}, items={}",
                record.topic(), record.partition(), record.offset(),
                event.orderId(), event.customerId(), event.amount(), event.items().size());
    }
}
