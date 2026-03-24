# Kafka Order Consumer

[![GitHub](https://img.shields.io/badge/Java-21-blue)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-green)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-4.1.2-red)](https://kafka.apache.org/)

Консьюмер для асинхронной обработки заказов. Читает сообщения из Kafka, обрабатывает ошибки с ретраями и отправляет в DLT.

## Функциональность

- Чтение сообщений из топика `orders-v2`
- Автоматические ретраи (3 попытки с интервалом 1 секунда)
- Отправка ошибочных сообщений в DLT `orders-v2.DLT`
- Health check эндпоинт

## Health Check

```http
GET http://localhost:8080/actuator/health
```

## Механизм ретраев

1. Сообщение не коммитится (`enable-auto-commit: false`)
2. При ошибке консьюмер повторяет обработку до 3 раз
3. После 3 неудачных попыток сообщение отправляется в DLT

## Сборка

```bash
mvn clean package
```

## Запуск

```bash
java -jar target/notification-service-0.0.1-SNAPSHOT.jar
```

Сервис будет доступен на порту 8080.

## Конфигурация

`src/main/resources/application.yml`:

```yaml
spring:
  kafka:
    consumer:
      group-id: notification-group-v100
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.value.default.type: org.example.dto.OrderRequest
        spring.json.trusted.packages: "*"

server:
  port: 8080
```

## Связанные репозитории

| Репозиторий | Описание | Ссылка |
|-------------|----------|--------|
| **Главный репозиторий** | Документация и инфраструктура | [kafka-order-processing](https://github.com/antonmalov/kafka-order-processing) |
| **common-dto** | Общие DTO | [kafka-order-common-dto](https://github.com/antonmalov/kafka-order-common-dto) |
| **producer** | Продюсер (REST → Kafka) | [kafka-order-producer](https://github.com/antonmalov/kafka-order-producer) |
| **e2e-tests** | E2E тесты | [kafka-order-e2e-tests](https://github.com/antonmalov/kafka-order-e2e-tests) |

## Лицензия

MIT