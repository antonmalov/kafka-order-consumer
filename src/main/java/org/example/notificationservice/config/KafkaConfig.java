package org.example.notificationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {


    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        return new DefaultErrorHandler((record, ex) -> {
            System.out.println("=== ERROR HANDLER CALLED ===");
            System.out.println("Record: " + record.value());
            System.out.println("Exception: " + ex.getMessage());
            try {
                String json = new ObjectMapper().writeValueAsString(record.value());
                kafkaTemplate.send(record.topic() + ".DLT", (String) record.key(), json);
                System.out.println("Sent to DLT: " + json);
            } catch (Exception e) {
                System.err.println("Failed to send to DLT: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }, new FixedBackOff(1000L, 3));
    }
}
