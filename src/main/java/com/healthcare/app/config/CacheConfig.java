package com.healthcare.app.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for application caching.
 * Sets up caches for external API responses to improve performance.
 */
@Configuration
@EnableCaching
@Log4j2
public class CacheConfig {

    /**
     * Creates a cache manager with predefined caches for the application.
     *
     * @return the configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing cache manager for application");
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("users"),
                new ConcurrentMapCache("userById")
        ));
        return cacheManager;
    }
}
