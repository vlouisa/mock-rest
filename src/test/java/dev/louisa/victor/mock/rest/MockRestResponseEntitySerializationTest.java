package dev.louisa.victor.mock.rest;

import dev.louisa.victor.mock.rest.dto.Animal;
import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

class MockRestResponseEntitySerializationTest extends BaseTest<User> {
    @Test
    void shouldReturnNullWhenControllerMethodReturnTypeIsVoid() {
        assertThatCode(
                () -> mockRest
                .post("/api/v1/users/", GRISWOLD)
                .send()
                .andReturn(User.class))
        .isInstanceOf(AssertionError.class)
        .hasMessageContaining("Failed to parse response body to User");
    }

    @Test
    void shouldThrowWhenControllerMethodReturnNull() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(OK, null));

        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .send()
                        .andReturn(User.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Failed to parse response body to User");
    }

    @Test
    void shouldReturnNullWhenParsingToVoid() throws Exception {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(OK, GRISWOLD));

        var response = mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .send()
                        .andReturn(Void.class);
    
        assertThat(response).isNull();
    }

    @Test
    void shouldThrowWhenResponseBodyIsNotFormattedAsExpected() {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(OK, GRISWOLD));
        
        assertThatCode(
                () -> mockRest
                        .get("/api/v1/users/{id}", GRISWOLD_UUID)
                        .send()
                        .andReturn(Animal.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Failed to parse response body to Animal:");
    }
}
