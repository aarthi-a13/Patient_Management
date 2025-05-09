package com.healthcare.app.controller;

import com.healthcare.app.model.User;
import com.healthcare.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing User resources.
 * This controller provides CRUD operations for users via the JSONPlaceholder API.
 */
@RestController
@RequestMapping("/api/v1/users")
@Log4j2
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/v1/users : Get all users
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of users in the body
     */
    @Operation(summary = "Get all users", description = "Retrieve a list of all users from JSONPlaceholder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("REST request to get all Users");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * GET /api/v1/users/{id} : Get user by id
     * 
     * @param id the id of the user to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the user, or with status 404 (Not Found)
     */
    @Operation(summary = "Get a user by ID", description = "Retrieve a user by their unique ID from JSONPlaceholder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID of user to be retrieved", required = true) 
            @PathVariable Long id) {
        log.info("REST request to get User : {}", id);
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * POST /api/v1/users : Create a new user
     * 
     * @param user the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user
     */
    @Operation(summary = "Create a new user", description = "Add a new user via JSONPlaceholder API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
    })
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to be created", required = true, 
                    schema = @Schema(implementation = User.class)) 
            @RequestBody User user) {
        log.info("REST request to save User : {}", user);
        User result = userService.createUser(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /**
     * PUT /api/v1/users/{id} : Updates an existing user
     * 
     * @param id the id of the user to update
     * @param user the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 404 (Not Found) if the user is not found
     */
    @Operation(summary = "Update an existing user", description = "Update an existing user's information via JSONPlaceholder API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID of user to be updated", required = true) 
            @PathVariable Long id,
            @Parameter(description = "Updated user object", required = true, 
                    schema = @Schema(implementation = User.class)) 
            @RequestBody User user) {
        log.info("REST request to update User : {}, {}", id, user);
        User result = userService.updateUser(id, user);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * DELETE /api/v1/users/{id} : Delete a user
     * 
     * @param id the id of the user to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @Operation(summary = "Delete a user", description = "Delete a user from the system via JSONPlaceholder API.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of user to be deleted", required = true) 
            @PathVariable Long id) {
        log.info("REST request to delete User : {}", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
