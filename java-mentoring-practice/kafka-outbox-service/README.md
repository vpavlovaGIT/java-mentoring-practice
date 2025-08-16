# Kafka Outbox Service
#### Сервис для доставки событий в **Apache Kafka**.
Принимает события через REST API, сохраняет их в базу данных (H2), а затем по расписанию отправляет их в Kafka.

Включает:
- REST-контроллер для приёма событий
- Хранение в таблице `OutboxEvent`
- Планировщик (`@Scheduled`) для отправки непрочитанных событий
- Kafka Producer & Consumer
- Docker Compose окружение (Zookeeper, Kafka, Kafka UI)

## Запуск проекта
### 1. Запуск инфраструктуры (Kafka, Zookeeper, Kafka UI)
```bash
docker-compose up -d
```
- Zookeeper на localhost:2181
- Kafka на localhost:9092
- Kafka UI на http://localhost:8081

### 2. Сборка и запуск Spring Boot приложения
```bash
mvn clean package
java -jar target/kafka-outbox-service-1.0.0.jar
```
Приложение поднимется на http://localhost:8080
### Отправка события в Outbox
Можно использовать Postman для отправки запроса
```http
POST /api/events
Content-Type: application/json
"Привет, Kafka!"
```
Через curl
```bash
curl -X POST http://localhost:8080/api/events \
     -H "Content-Type: application/json" \
     -d '"Привет, Kafka!"'
```
Ответ:
``` vbnet
Event received and saved to outbox.
```
### Проверка Outbox таблицы в H2-консоль

```bash
http://localhost:8080/h2-console
```
JDBC URL: jdbc:h2:mem:testdb

**Запись в логах спустя 60 сек**
```
Received message from Kafka: Привет, Kafka!
```


