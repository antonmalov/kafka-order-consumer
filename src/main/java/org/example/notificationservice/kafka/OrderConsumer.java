package org.example.notificationservice.kafka;


import org.example.dto.OrderRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @KafkaListener(topics = "orders-v2")
    public void listen(OrderRequest order) {
        System.out.println("=== CONSUMER RECEIVED: " + order);
        if ("fail".equals(order.getProduct())) {
            System.out.println("=== THROWING EXCEPTION for product=fail ===");
            throw new RuntimeException("Test error");
        }
        System.out.println("=== PROCESSED SUCCESSFULLY ===");
    }

    @KafkaListener(topics = "orders-v2.DLT")
    public void listenDlq(OrderRequest order) {
        System.out.println("=== DLQ MESSAGE: " + order);
    }
}