package org.example.notificationservice.kafka;


import org.example.dto.OrderRequest;
import org.example.notificationservice.entity.OrderEntity;
import org.example.notificationservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class OrderConsumer {

    private final OrderRepository orderRepository;

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "orders-v2")
    @Transactional
    public void listen(OrderRequest order) {
        System.out.println("=== CONSUMER RECEIVED: " + order);

        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setProduct(order.getProduct());
        entity.setQuantity(order.getQuantity());
        entity.setCreatedAt(LocalDateTime.now());
        orderRepository.save(entity);
        System.out.println("Saved to database: " + entity.getId());

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