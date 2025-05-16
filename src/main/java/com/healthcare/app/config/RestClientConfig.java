package com.healthcare.app.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for RestClient used to communicate with external APIs.
 */
@Configuration
@Log4j2
public class RestClientConfig {

    /**
     * Creates a RestClient bean for making HTTP requests to external APIs.
     * 
     * @return configured RestClient instance
     */
    @Bean
    public RestClient restClient() {
        log.info("Initializing RestClient for external API communication");
        return RestClient.builder().build();
    }
}
