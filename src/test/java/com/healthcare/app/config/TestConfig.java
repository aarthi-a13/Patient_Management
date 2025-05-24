package com.healthcare.app.config;

import com.health.util.event.UserEvent;
import com.health.util.kafka.producer.EventProducerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that disables Kafka auto-configuration and provides a no-op implementation
 * of the EventProducerService to prevent any Kafka connections during tests.
 */
@Configuration
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Log4j2
public class TestConfig {

    /**
     * Creates a no-op EventProducerService bean for testing.
     * This implementation logs events but does not attempt to connect to Kafka.
     *
     * @return A no-op EventProducerService
     */
    @Bean
    @Primary
    public EventProducerService eventProducerService() {
        return new NoOpEventProducerService();
    }
    
    /**
     * A no-operation implementation of EventProducerService for testing.
     * This implementation logs events but does not attempt to connect to Kafka.
     */
    private static class NoOpEventProducerService extends EventProducerService {
        
        public NoOpEventProducerService() {
            // Pass null to the parent constructor - this would normally cause issues,
            // but since we're overriding all methods that use the KafkaTemplate, it's safe
            super(null);
        }
        
        @Override
        public void sendUserEvent(UserEvent userEvent) {
            // Just log the event without attempting to send it to Kafka
            if (userEvent == null) {
                log.info("[TEST] Received null event in NoOpEventProducerService");
                return;
            }
            
            if (userEvent.userData() == null) {
                log.info("[TEST] Received event with null userData in NoOpEventProducerService: type={}", 
                         userEvent.eventType());
                return;
            }
            
            if (userEvent.userData().id() == null) {
                log.info("[TEST] Received event with null userData.id in NoOpEventProducerService: type={}", 
                         userEvent.eventType());
                return;
            }
            
            log.info("[TEST] NoOpEventProducerService received event: type={}, userId={}", 
                     userEvent.eventType(), userEvent.userData().id());
        }
    }
}
