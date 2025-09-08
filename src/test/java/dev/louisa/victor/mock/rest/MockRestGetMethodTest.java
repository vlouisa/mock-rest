package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.louisa.victor.mock.rest.controller.UserController;
import dev.louisa.victor.mock.rest.dto.Animal;
import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class MockRestGetMethodTest extends BaseTest {
    private static final String GUYBRUSH_UUID = "d45af7e2-3c4b-11ee-be56-0242ac120002";
    private static final User GUYBRUSH = User.builder()
            .id(UUID.fromString("d45af7e2-3c4b-11ee-be56-0242ac120002"))
            .name("Guybrush Threepwood")
            .build();
    private static final Map<String, String> RESPONSE_HEADERS = Map.of(
            "x-response-header", "Monkey Island 2 rules!",
            "Content-Type", "application/json"
    );

    private MockRest mockRest;

    @Mock
    private UserController controller;


    @BeforeEach
    void setup() {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mockRest = new MockRest(mockMvc, new ObjectMapper());
    }

    @Test
    void shouldCallTheEndpointOnTheController() throws Exception {
        when(controller.getUser(GUYBRUSH_UUID))
                .thenReturn(response(OK, GUYBRUSH, RESPONSE_HEADERS));

        mockRest
                .get("/api/v1/users/{id}", GUYBRUSH_UUID)
                .withRequestHeader("x-test-header", "Monkey Island 1 rules!")
                .withJwt("C9v9gF8o4VK6FywhVoquh7DpnFohd5n6I4qNrZoisXo=")
                .expectResponseStatus(OK)
                .expectResponseHeader("x-response-header", "Monkey Island 2 rules!")
                .expectResponseHeader("Content-Type", "application/json")
                .expectResponseBody(User.class);

        verify(controller).getUser(GUYBRUSH_UUID);
    }


    @Test
    void shouldReturnResponseInExpectedBodyFormatWhenEverythingIsOkay() throws Exception {
        when(controller.getUser(GUYBRUSH_UUID))
                .thenReturn(response(OK, GUYBRUSH));

        var response = mockRest
                .get("/api/v1/users/{id}", GUYBRUSH_UUID)
                .expectResponseStatus(OK)
                .expectResponseBody(User.class);

        assertThat(response).isEqualTo(GUYBRUSH);
    }

    @Test
    void shouldThrowWhenExpectedBodyFormatIsDifferent() throws Exception {
        when(controller.getUser(GUYBRUSH_UUID))
                .thenReturn(response(OK, GUYBRUSH));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GUYBRUSH_UUID)
                        .expectResponseStatus(OK)
                        .expectResponseBody(Animal.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Failed to parse response body to Animal");

    }

    @Test
    void shouldThrowAssertionErrorWhenExpectedStatusIsDifferent() {
        when(controller.getUser(GUYBRUSH_UUID))
                .thenReturn(response(OK, GUYBRUSH));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GUYBRUSH_UUID)
                        .expectResponseStatus(NOT_FOUND)
                        .expectResponseBody(User.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("<404> but was:<200>");
    }

    @Test
    void shouldThrowAssertionErrorWhenNoBodyExpectedButBodyIsReturned() {
        when(controller.getUser(GUYBRUSH_UUID))
                .thenReturn(response(OK, GUYBRUSH));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GUYBRUSH_UUID)
                        .expectResponseStatus(OK)
                        .expectNoResponseBody())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected no body, but response contained:")
                .hasMessageContaining("d45af7e2-3c4b-11ee-be56-0242ac120002");

    }


    private ResponseEntity<User> response(HttpStatus httpStatus, User user) {
        return response(httpStatus, user, Map.of());
    }
    
    private ResponseEntity<User> response(HttpStatus httpStatus, User user, Map<String, String> headers) {

        return ResponseEntity
                .status(httpStatus)
                .headers(h -> headers.forEach(h::add))
                .body(user);
    }

    private UUID uuid(String value) {
        return UUID.fromString(value);
    }

}
