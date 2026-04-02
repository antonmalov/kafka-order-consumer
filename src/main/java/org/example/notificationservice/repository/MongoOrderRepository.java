package org.example.notificationservice.repository;

import org.example.notificationservice.entity.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoOrderRepository extends MongoRepository<OrderDocument, String> {
}