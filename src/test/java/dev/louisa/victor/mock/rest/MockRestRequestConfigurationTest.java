package dev.louisa.victor.mock.rest;

import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class MockRestRequestConfigurationTest extends BaseTest<User> {
    @Test
    void shouldSetTokenOnRequest() throws Exception {
        mockRest
                .get("/api/v1/users/{id}/details", GRISWOLD_UUID)
                .withJwt("7U5OubfsP+RWZZpNY9HMP6h5maNQsug8ks5U8n1rQtA=")
                .send();

        verify(controller).getUser(GRISWOLD_UUID, "Bearer 7U5OubfsP+RWZZpNY9HMP6h5maNQsug8ks5U8n1rQtA=");
    }

    @Test
    void shouldSetHeaderOnRequest() throws Exception {
        mockRest
                .get("/api/v1/users/{id}/details", GRISWOLD_UUID)
                .withRequestHeader("Authorization","7U5OubfsP+RWZZpNY9HMP6h5maNQsug8ks5U8n1rQtA=")
                .send();

        verify(controller).getUser(GRISWOLD_UUID, "7U5OubfsP+RWZZpNY9HMP6h5maNQsug8ks5U8n1rQtA=");
    }

    @Test
    void shouldNotThrowWhenResponseStatusIsAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(I_AM_A_TEAPOT, GRISWOLD));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .expectResponseStatus(I_AM_A_TEAPOT)
                        .send())
                .doesNotThrowAnyException();

    }

    @Test
    void shouldThrowWhenResponseStatusIsNotAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(I_AM_A_TEAPOT, GRISWOLD));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .expectResponseStatus(BAD_GATEWAY)
                        .send())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Response status expected:<502> but was:<418>");

    }

    @Test
    void shouldNotThrowWhenResponseHeadersAreAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(I_AM_A_TEAPOT, GRISWOLD, RESPONSE_HEADERS));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .expectResponseHeader("x-response-header", "Monkey Island 2 rules!")
                        .expectResponseHeader("Content-Type", "application/json")
                        .send())
                .doesNotThrowAnyException();

    }

    @Test
    void shouldThrowWhenResponseHeadersAreNotAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(I_AM_A_TEAPOT, GRISWOLD));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .expectResponseHeader("x-response-header", "Monkey Island 2 rules!")
                        .expectResponseHeader("Content-Type", "application/json")
                        .send())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected header 'x-response-header' but it was not found");

    }

    @Test
    void shouldThrowWhenResponseHeaderValueIsNotAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(I_AM_A_TEAPOT, GRISWOLD, RESPONSE_HEADERS));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .expectResponseHeader("x-response-header", "GTA VI is coming out in 2026!")
                        .send())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected header 'x-response-header' value 'GTA VI is coming out in 2026!', but was 'Monkey Island 2 rules!'");
    }
}
