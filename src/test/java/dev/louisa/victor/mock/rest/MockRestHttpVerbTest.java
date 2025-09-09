package dev.louisa.victor.mock.rest;

import dev.louisa.victor.mock.rest.dto.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

class MockRestHttpVerbTest extends BaseTest<User> {
    @Test
    void shouldCallTheGetEndpointOnTheController() throws Exception {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(OK, GRISWOLD));

        mockRest
                .get("/api/v1/users/{id}", GRISWOLD_UUID)
                .send();

        verify(controller).getUser(GRISWOLD_UUID);
    }

    @Test
    void shouldReturnExpectedResponseBody() throws Exception {
        when(controller.getUser(GRISWOLD_UUID))
                .thenReturn(response(OK, GRISWOLD));

        var response = mockRest
                .get("/api/v1/users/{id}", GRISWOLD_UUID)
                .send()
                .andReturn(User.class);

        assertThat(response)
                .isEqualTo(GRISWOLD);
    }

    @Test
    void shouldCallThePostEndpointOnTheController() throws Exception {
        mockRest
                .post("/api/v1/users")
                .body(GRISWOLD)
                .send();

        verify(controller).postUser(GRISWOLD);
    }

    @Test
    void shouldCallThePutEndpointOnTheController() throws Exception {
        mockRest
                .put("/api/v1/users/{id}", GRISWOLD_UUID)
                .body(GRISWOLD)
                .send();

        verify(controller).putUser(GRISWOLD_UUID, GRISWOLD);
    }

    @Test
    void shouldCallThePatchEndpointOnTheController() throws Exception {
        mockRest
                .patch("/api/v1/users/{id}", GRISWOLD_UUID)
                .body(GRISWOLD)
                .send();

        verify(controller).patchUser(GRISWOLD_UUID, GRISWOLD);
    }

    @Test
    void shouldCallTheDeleteEndpointOnTheController() throws Exception {
        mockRest
                .delete("/api/v1/users/{id}", GRISWOLD_UUID)
                .send();

        verify(controller).deleteUser(GRISWOLD_UUID);
    }
}
