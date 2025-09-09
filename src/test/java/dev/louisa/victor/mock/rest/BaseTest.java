package dev.louisa.victor.mock.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.louisa.victor.mock.rest.controller.UserController;
import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Tag("unit-test")
public class BaseTest<T> {
    protected static final String GRISWOLD_UUID = "d45af7e2-3c4b-11ee-be56-0242ac120002";
    protected static final User GRISWOLD = User.builder()
            .id(UUID.fromString("d45af7e2-3c4b-11ee-be56-0242ac120002"))
            .name("Griswold Goudsoup")
            .build();
    protected static final String GUYBRUSH_UUID = "82fe4f3e-3c4b-11ee-be56-0242ac120002";
    protected static final User GUYBRUSH = User.builder()
            .id(UUID.fromString("82fe4f3e-3c4b-11ee-be56-0242ac120002"))
            .name("Guybrush Threepwood")
            .build();
    protected static final Map<String, String> RESPONSE_HEADERS = Map.of(
            "x-response-header", "Monkey Island 2 rules!",
            "Content-Type", "application/json"
    );

    protected MockRest mockRest;

    @Mock
    protected UserController controller;
    
    @BeforeEach
    void setup() {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        mockRest = new MockRest(mockMvc, new ObjectMapper());
    }


    protected ResponseEntity<T> response(HttpStatus httpStatus, T object) {
        return response(httpStatus, object, Map.of());
    }

    protected ResponseEntity<T> response(HttpStatus httpStatus, T object, Map<String, String> headers) {

        return ResponseEntity
                .status(httpStatus)
                .headers(h -> headers.forEach(h::add))
                .body(object);
    }
}
