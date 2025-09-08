package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.louisa.victor.mock.rest.controller.UserController;
import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MockRestPostMethodTest {
    private static final String GUYBRUSH_UUID = "d45af7e2-3c4b-11ee-be56-0242ac120002";
    private static final User GUYBRUSH = User.builder()
            .id(UUID.fromString("d45af7e2-3c4b-11ee-be56-0242ac120002"))
            .name("Guybrush Threepwood")
            .build();

    private MockRest mockRest;

    @Mock
    private UserController controller;


    @BeforeEach
    void setup() {
        final MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mockRest = new MockRest(mockMvc, new ObjectMapper());
    }

    @Test
    void shouldNotThrowWhenEverythingIsOkay() throws Exception {
        assertThatCode(
                () -> mockRest
                        .post("/api/v1/users")
                        .body(GUYBRUSH)
                        .expectResponseStatus(NO_CONTENT)
                        .expectNoResponseBody())
                .doesNotThrowAnyException();

        verify(controller).createUser(GUYBRUSH);
    }

    @Test
    void shouldThrowWhenBodyExpected() throws Exception {
        assertThatCode(
                () -> mockRest
                        .post("/api/v1/users")
                        .body(GUYBRUSH)
                        .expectResponseStatus(NO_CONTENT)
                        .expectResponseBody(User.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected body of type 'User', but no body present")
        ;

        verify(controller).createUser(GUYBRUSH);
    }



    private ResponseEntity<User> response(HttpStatus httpStatus, User user) {

        return ResponseEntity
                .status(httpStatus)
                .body(user);
    }

    private UUID uuid(String value) {
        return UUID.fromString(value);
    }

}
