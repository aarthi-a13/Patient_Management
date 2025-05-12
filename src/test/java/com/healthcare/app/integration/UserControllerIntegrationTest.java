package com.healthcare.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.healthcare.app.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "jsonplaceholder.api.url=http://localhost:8082"
})
class UserControllerIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setupWireMock() {
        // Start WireMock server on port 8082 to mock the JSONPlaceholder API
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8082));
        wireMockServer.start();
        configureFor("localhost", 8082);
    }

    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Reset WireMock before each test
        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("GET /api/v1/users - Should return all users")
    @WithMockUser(roles = {"USER"})
    void getAllUsers() throws Exception {
        // Arrange - Setup WireMock to respond to the external API call
        stubFor(get(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" +
                                "{\"id\": 1, \"name\": \"John Doe\", \"username\": \"johndoe\", \"email\": \"john@example.com\"}," +
                                "{\"id\": 2, \"name\": \"Jane Smith\", \"username\": \"janesmith\", \"email\": \"jane@example.com\"}" +
                                "]")));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

        // Verify the request was made to the external API
        verify(getRequestedFor(urlEqualTo("/users")));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should return user by ID")
    @WithMockUser(roles = {"USER"})
    void getUserById() throws Exception {
        // Arrange - Setup WireMock to respond to the external API call
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{" +
                                "\"id\": 1," +
                                "\"name\": \"John Doe\"," +
                                "\"username\": \"johndoe\"," +
                                "\"email\": \"john@example.com\"," +
                                "\"address\": {" +
                                "  \"street\": \"123 Main St\"," +
                                "  \"suite\": \"Apt 4B\"," +
                                "  \"city\": \"Boston\"," +
                                "  \"zipcode\": \"02108\"," +
                                "  \"geo\": {" +
                                "    \"lat\": \"42.3601\"," +
                                "    \"lng\": \"-71.0589\"" +
                                "  }" +
                                "}," +
                                "\"phone\": \"555-123-4567\"," +
                                "\"website\": \"johndoe.com\"," +
                                "\"company\": {" +
                                "  \"name\": \"ABC Corp\"," +
                                "  \"catchPhrase\": \"We do things\"," +
                                "  \"bs\": \"synergize scalable supply-chains\"" +
                                "}" +
                                "}")));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")))
                .andExpect(jsonPath("$.address.city", is("Boston")))
                .andExpect(jsonPath("$.company.name", is("ABC Corp")));

        // Verify the request was made to the external API
        verify(getRequestedFor(urlEqualTo("/users/1")));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should return 404 when user not found")
    @WithMockUser(roles = {"USER"})
    void getUserById_NotFound() throws Exception {
        // Arrange - Setup WireMock to respond with 404 for non-existent user
        stubFor(get(urlEqualTo("/users/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"User not found\"}")));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Verify the request was made to the external API
        verify(getRequestedFor(urlEqualTo("/users/999")));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should create a new user")
    @WithMockUser(roles = {"USER"})
    void createUser() throws Exception {
        // Arrange - Create a user to send in the request
        User.Geo geo = new User.Geo("42.3601", "-71.0589");
        User.Address address = new User.Address("123 Main St", "Apt 4B", "Boston", "02108", geo);
        User.Company company = new User.Company("ABC Corp", "We do things", "synergize scalable supply-chains");
        User newUser = new User(null, "John Doe", "johndoe", "john@example.com", address, "555-123-4567", "johndoe.com", company);

        // Setup WireMock to respond to the external API call
        stubFor(post(urlEqualTo("/users"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{" +
                                "\"id\": 11," +
                                "\"name\": \"John Doe\"," +
                                "\"username\": \"johndoe\"," +
                                "\"email\": \"john@example.com\"," +
                                "\"address\": {" +
                                "  \"street\": \"123 Main St\"," +
                                "  \"suite\": \"Apt 4B\"," +
                                "  \"city\": \"Boston\"," +
                                "  \"zipcode\": \"02108\"," +
                                "  \"geo\": {" +
                                "    \"lat\": \"42.3601\"," +
                                "    \"lng\": \"-71.0589\"" +
                                "  }" +
                                "}," +
                                "\"phone\": \"555-123-4567\"," +
                                "\"website\": \"johndoe.com\"," +
                                "\"company\": {" +
                                "  \"name\": \"ABC Corp\"," +
                                "  \"catchPhrase\": \"We do things\"," +
                                "  \"bs\": \"synergize scalable supply-chains\"" +
                                "}" +
                                "}")));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        // Verify the request was made to the external API
        verify(postRequestedFor(urlEqualTo("/users"))
                .withRequestBody(containing("John Doe")));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Should update an existing user")
    @WithMockUser(roles = {"USER"})
    void updateUser() throws Exception {
        // Arrange - Create a user to send in the update request
        User.Geo geo = new User.Geo("42.3601", "-71.0589");
        User.Address address = new User.Address("456 Updated St", "Suite 10", "New York", "10001", geo);
        User.Company company = new User.Company("Updated Corp", "New catchphrase", "new bs");
        User updatedUser = new User(1L, "John Updated", "johnupdated", "updated@example.com", address, "555-999-8888", "updated.com", company);

        // Setup WireMock to respond to the GET request (for checking if user exists)
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"John Doe\"}")));

        // Setup WireMock to respond to the PUT request
        stubFor(put(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{" +
                                "\"id\": 1," +
                                "\"name\": \"John Updated\"," +
                                "\"username\": \"johnupdated\"," +
                                "\"email\": \"updated@example.com\"," +
                                "\"address\": {" +
                                "  \"street\": \"456 Updated St\"," +
                                "  \"suite\": \"Suite 10\"," +
                                "  \"city\": \"New York\"," +
                                "  \"zipcode\": \"10001\"," +
                                "  \"geo\": {" +
                                "    \"lat\": \"42.3601\"," +
                                "    \"lng\": \"-71.0589\"" +
                                "  }" +
                                "}," +
                                "\"phone\": \"555-999-8888\"," +
                                "\"website\": \"updated.com\"," +
                                "\"company\": {" +
                                "  \"name\": \"Updated Corp\"," +
                                "  \"catchPhrase\": \"New catchphrase\"," +
                                "  \"bs\": \"new bs\"" +
                                "}" +
                                "}")));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.address.city", is("New York")))
                .andExpect(jsonPath("$.company.name", is("Updated Corp")));

        // Verify the requests were made to the external API
        verify(getRequestedFor(urlEqualTo("/users/1")));
        verify(putRequestedFor(urlEqualTo("/users/1"))
                .withRequestBody(containing("John Updated")));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Should delete a user")
    @WithMockUser(roles = {"USER"})
    void deleteUser() throws Exception {
        // Arrange - Setup WireMock to respond to the GET request (for checking if user exists)
        stubFor(get(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": 1, \"name\": \"John Doe\"}")));

        // Setup WireMock to respond to the DELETE request
        stubFor(delete(urlEqualTo("/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Verify the requests were made to the external API
        verify(getRequestedFor(urlEqualTo("/users/1")));
        verify(deleteRequestedFor(urlEqualTo("/users/1")));
    }
}
