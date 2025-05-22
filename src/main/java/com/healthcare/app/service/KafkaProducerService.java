package com.healthcare.app.service;


import com.healthcare.app.model.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing and sending messages to Kafka topics.
 * This service uses Spring Kafka's KafkaTemplate for message production.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class KafkaProducerService {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private static final String USER_TOPIC = "user-topic";

    /**
     * Sends a UserEvent to the predefined user Kafka topic.
     *
     * @param userEvent The UserEvent to be sent.
     */
    public void sendUserEvent(UserEvent userEvent) {
        try {
            // Ensure userData and its ID are not null to prevent NullPointerException
            String userId = (userEvent.userData() != null && userEvent.userData().id() != null)
                            ? userEvent.userData().id().toString()
                            : "unknown_user_id"; // Or handle as an error, or generate a UUID

            log.info("Sending UserEvent to topic '{}' with key '{}': {}", USER_TOPIC, userId, userEvent);
            kafkaTemplate.send(USER_TOPIC, userId, userEvent);
            log.info("UserEvent sent successfully to topic '{}'", USER_TOPIC);
        } catch (Exception e) {
            log.error("Error sending UserEvent to topic '{}': {}", USER_TOPIC, userEvent, e);
            // Consider rethrowing a custom exception or specific handling
        }
    }
}
