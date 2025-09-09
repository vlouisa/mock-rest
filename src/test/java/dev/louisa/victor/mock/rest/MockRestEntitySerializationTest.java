package dev.louisa.victor.mock.rest;

import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class MockRestEntitySerializationTest extends BaseTest<User> {
    @Test
    void shouldReturnUser() throws Exception {
        when(controller.getUserRaw(GRISWOLD_UUID))
                .thenReturn(GRISWOLD);

        var response = mockRest
                .get("/api/v1/users/{id}/raw", GRISWOLD_UUID)
                .send()
                .andReturn(User.class);

        assertThat(response).isEqualTo(GRISWOLD);
    }

    @Test
    void shouldThrowWhenEndpointExpectsParameterThatIsNotPassed() throws Exception {
        assertThatCode(
                () -> mockRest.get("/api/v1/users/{id}/raw")
                        .expectResponseStatus(BAD_REQUEST)
                        .send()
                        .andReturn(User.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not enough variable values available to expand 'id'");
    }

    @Test
    void shouldThrowWhenExpectedStatusIsDifferent() throws Exception {
        when(controller.getUserRaw(GRISWOLD_UUID))
                .thenReturn(GRISWOLD);

        assertThatCode(
                () -> mockRest.get("/api/v1/users/{id}/raw", GRISWOLD_UUID)
                        .expectResponseStatus(BAD_REQUEST)
                        .send()
                        .andReturn(User.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Response status expected:<400> but was:<200>");

    }

    @Test
    void shouldThrowWhenCallingNonExisytingEndpoint() throws Exception {
        assertThatCode(
                () -> mockRest.get("/api/v1/blablabla")
                        .expectResponseStatus(BAD_REQUEST)
                        .send()
                        .andReturn(User.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Response status expected:<400> but was:<404>");

    }

}
