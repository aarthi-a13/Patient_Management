package com.healthcare.app.service;

import com.healthcare.app.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Service for managing User entities via the JSONPlaceholder API.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final RestClient restClient;

    @Value("${jsonplaceholder.api.url:https://jsonplaceholder.typicode.com}")
    private String apiUrl;

    /**
     * Retrieves all users from the JSONPlaceholder API.
     * Results are cached to avoid redundant API calls for static data.
     *
     * @return a list of all users
     */
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        log.info("Cache miss for all users - fetching from JSONPlaceholder API");
        List<User> users = restClient.get()
                .uri(apiUrl + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("Successfully retrieved {} users from JSONPlaceholder API", users != null ? users.size() : 0);
        return users;
    }

    /**
     * Retrieves a user by ID from the JSONPlaceholder API.
     * Results are cached by user ID to avoid redundant API calls for static data.
     *
     * @param id the ID of the user to retrieve
     * @return the user with the specified ID
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @Cacheable(value = "userById", key = "#id")
    public User getUserById(Long id) {
        log.info("Cache miss for user ID: {} - fetching from JSONPlaceholder API", id);
        User user = restClient.get()
                .uri(apiUrl + "/users/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (request, response) -> {
                            throw new EntityNotFoundException("User not found with id: " + id);
                        })
                .body(User.class);
        log.info("Successfully retrieved user with ID: {} from JSONPlaceholder API", id);
        return user;
    }

    /**
     * Creates a new user via the JSONPlaceholder API.
     * Evicts the users cache to ensure consistency.
     *
     * @param user the user to create
     * @return the created user
     */
    @CacheEvict(value = "users", allEntries = true)
    @CachePut(value = "userById", key = "#result.id")
    public User createUser(User user) {
        log.info("Creating a new user via JSONPlaceholder API");
        User createdUser = restClient.post()
                .uri(apiUrl + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .body(User.class);
        log.info("Successfully created user with ID: {} via JSONPlaceholder API", createdUser != null ? createdUser.id() : "unknown");
        return createdUser;
    }

    /**
     * Updates an existing user via the JSONPlaceholder API.
     * Updates the cache for the specific user and evicts the users cache.
     *
     * @param id   the ID of the user to update
     * @param user the updated user data
     * @return the updated user
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @CacheEvict(value = "users", allEntries = true)
    @CachePut(value = "userById", key = "#id")
    public User updateUser(Long id, User user) {
        log.info("Updating user with ID: {} via JSONPlaceholder API", id);
        // First check if the user exists - this will use the cache if available
        getUserById(id);

        User updatedUser = restClient.put()
                .uri(apiUrl + "/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .body(User.class);
        log.info("Successfully updated user with ID: {} via JSONPlaceholder API", id);
        return updatedUser;
    }

    /**
     * Deletes a user by ID via the JSONPlaceholder API.
     * Evicts the user from both caches to maintain consistency.
     *
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if no user is found with the specified ID
     */
    @CacheEvict(cacheNames = {"users", "userById"}, allEntries = true)
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {} via JSONPlaceholder API", id);
        // First check if the user exists - this will use the cache if available
        getUserById(id);

        restClient.delete()
                .uri(apiUrl + "/users/{id}", id)
                .retrieve()
                .toBodilessEntity();
        log.info("Successfully deleted user with ID: {} via JSONPlaceholder API", id);
    }
}
